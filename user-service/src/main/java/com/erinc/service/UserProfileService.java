package com.erinc.service;

import com.erinc.dto.request.NewCreateUserRequestDto;
import com.erinc.dto.request.UpdateEmailOrUsernameRequestDto;
import com.erinc.dto.request.UserProfileUpdateRequestDto;
import com.erinc.exception.ErrorType;
import com.erinc.exception.UserManagerException;
import com.erinc.manager.IAuthManager;
import com.erinc.mapper.IUserMapper;
import com.erinc.repository.IUserProfileRepository;
import com.erinc.repository.entity.UserProfile;
import com.erinc.repository.enums.EStatus;
import com.erinc.utility.JwtTokenManager;
import com.erinc.utility.ServiceManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserProfileService extends ServiceManager<UserProfile,Long> {

    private final IUserProfileRepository userProfileRepository;

    private final JwtTokenManager jwtTokenManager;
    private final CacheManager cacheManager;
    private final IAuthManager authManager;
    public UserProfileService(IUserProfileRepository userProfileRepository, JwtTokenManager jwtTokenManager, CacheManager cacheManager, IAuthManager authManager) {
        super(userProfileRepository);
        this.userProfileRepository = userProfileRepository;
        this.jwtTokenManager = jwtTokenManager;
        this.cacheManager = cacheManager;
        this.authManager = authManager;
    }

    public Boolean createUser(NewCreateUserRequestDto dto) {
        try {
            save(IUserMapper.INSTANCE.toUserProfile(dto));
            return  true;
        }catch (Exception e){
            throw  new UserManagerException(ErrorType.USER_NOT_CREATED);
        }

    }

    public Boolean activateStatus(Long authId) {
        Optional<UserProfile> userProfile=userProfileRepository.findOptionalByAuthId(authId);
        if (userProfile.isEmpty()){
            throw new UserManagerException(ErrorType.USER_NOT_FOUND);
        }
        userProfile.get().setStatus(EStatus.ACTIVE);
        update(userProfile.get());
        return true;
    }



    public Boolean update(UserProfileUpdateRequestDto dto){
        Optional<Long> authId=jwtTokenManager.getIdFromToken(dto.getToken());
        if (authId.isEmpty()){
            throw new UserManagerException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> userProfile=userProfileRepository.findOptionalByAuthId(authId.get());
        if (userProfile.isEmpty()){
            throw new UserManagerException(ErrorType.USER_NOT_FOUND);
        }
        cacheManager.getCache("findbyusername").evict(userProfile.get().getUsername().toLowerCase());
        if ( !dto.getUsername().equals(userProfile.get().getUsername())||!dto.getEmail().equals(userProfile.get().getEmail())){
            userProfile.get().setUsername(dto.getUsername());
            userProfile.get().setEmail(dto.getEmail());
            UpdateEmailOrUsernameRequestDto updateEmailOrUsernameRequestDto=IUserMapper.INSTANCE.toUpdateEmailOrUsernameRequestDto(dto);
            updateEmailOrUsernameRequestDto.setAuthId(authId.get());
            authManager.updateEmailOrUsername(updateEmailOrUsernameRequestDto);
        }
        // hi
        userProfile.get().setPhone(dto.getPhone());
        userProfile.get().setAvatar(dto.getAvatar());
        userProfile.get().setAddress(dto.getAddress());
        userProfile.get().setAbout(dto.getAbout());
        update(userProfile.get());
        return  true;
    }

    public Boolean delete(Long authId) {
        Optional<UserProfile> userProfile=userProfileRepository.findOptionalByAuthId(authId);
        if (userProfile.isEmpty()){
            throw new UserManagerException(ErrorType.USER_NOT_FOUND);
        }
        userProfile.get().setStatus(EStatus.DELETED);
        update(userProfile.get());
        return  true;
    }

    @Cacheable(value = "findByUsername",key ="#username.toLowerCase()")
    public UserProfile findByUsername(String username){
        Optional<UserProfile> userProfile=userProfileRepository.findOptionalByUsernameIgnoreCase(username);
        if(userProfile.isEmpty())
            throw new UserManagerException(ErrorType.USER_NOT_FOUND);
        return userProfile.get();
    }


}
