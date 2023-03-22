package com.erinc.service;

import com.erinc.dto.request.ActivateRequestDto;
import com.erinc.dto.request.LoginRequestDto;
import com.erinc.dto.request.RegisterRequestDto;
import com.erinc.dto.request.UpdateEmailOrUsernameRequestDto;
import com.erinc.dto.response.RegisterResponseDto;
import com.erinc.exception.AuthManagerException;
import com.erinc.exception.ErrorType;
import com.erinc.manager.IUserManager;
import com.erinc.mapper.IAuthMapper;
import com.erinc.rabbitmq.producer.RegisterMailProducer;
import com.erinc.rabbitmq.producer.RegisterProducer;
import com.erinc.repository.IAuthRepository;
import com.erinc.repository.entity.Auth;
import com.erinc.repository.enums.ERole;
import com.erinc.repository.enums.EStatus;
import com.erinc.utility.CodeGenerator;
import com.erinc.utility.JwtTokenManager;
import com.erinc.utility.ServiceManager;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthService extends ServiceManager<Auth,Long> {

    private final IAuthRepository authRepository;
    private final RegisterProducer registerProducer;
    private final RegisterMailProducer registerMailProducer;
    private final IUserManager userManager;
    private final CacheManager cacheManager;
    private final JwtTokenManager jwtTokenManager;

    public AuthService(IAuthRepository authRepository, RegisterProducer registerProducer, RegisterMailProducer registerMailProducer, IUserManager userManager, CacheManager cacheManager, JwtTokenManager jwtTokenManager) {
        super(authRepository);
        this.authRepository = authRepository;
        this.registerProducer = registerProducer;
        this.registerMailProducer = registerMailProducer;
        this.userManager = userManager;
        this.cacheManager = cacheManager;
        this.jwtTokenManager = jwtTokenManager;
    }

    @Transactional
    public RegisterResponseDto register(RegisterRequestDto dto) {
        Auth auth= IAuthMapper.INSTANCE.toAuth(dto);
        auth.setActivationCode(CodeGenerator.genarateCode());

            try {
                //FeignClient ile haberleşme sağlanacak
                save(auth);
                userManager.createUser(IAuthMapper.INSTANCE.toNewCreateUserRequestDto(auth));
                cacheManager.getCache("findbyrole").evict(auth.getRole().toString().toUpperCase());
            }catch (Exception e){

           //     delete(auth);
                throw  new AuthManagerException(ErrorType.USER_NOT_CREATED);
            }
        RegisterResponseDto registerResponseDto=IAuthMapper.INSTANCE.toRegisterResponseDto(auth);
        return  registerResponseDto;
    }

    @Transactional
    public RegisterResponseDto registerWithRabbitMq(RegisterRequestDto dto) {
        Auth auth= IAuthMapper.INSTANCE.toAuth(dto);
        auth.setActivationCode(CodeGenerator.genarateCode());

        try {
            save(auth);
            //Rabbit MQ ile haberleşme sağlanacak..
            registerProducer.sendNewUser(IAuthMapper.INSTANCE.toRegisterModel(auth));
            registerMailProducer.sendActivationCode(IAuthMapper.INSTANCE.toRegisterMailModel(auth));
            cacheManager.getCache("findbyrole").evict(auth.getRole().toString().toUpperCase());
        }catch (Exception e){

            //     delete(auth);
            throw  new AuthManagerException(ErrorType.USER_NOT_CREATED);
        }
        RegisterResponseDto registerResponseDto=IAuthMapper.INSTANCE.toRegisterResponseDto(auth);
        return  registerResponseDto;
    }

    public String login(LoginRequestDto dto) {
        Optional<Auth> auth=authRepository.findOptionalByUsernameAndPassword(dto.getUsername(), dto.getPassword());

        if (auth.isEmpty()){
            throw  new AuthManagerException(ErrorType.LOGIN_ERROR);
        }
        if (!auth.get().getStatus().equals(EStatus.ACTIVE)){
            throw new AuthManagerException(ErrorType.ACCOUNT_NOT_ACTIVE);
        }

        return jwtTokenManager.createToken(auth.get().getId()
                ,auth.get().getRole()).orElseThrow(()-> {throw new AuthManagerException(ErrorType.TOKEN_NOT_CREATED);});

    }

    public Boolean activateStatus(ActivateRequestDto dto) {
        Optional<Auth> auth=findById(dto.getId());
        if (auth.isEmpty()){
            throw new AuthManagerException(ErrorType.USER_NOT_FOUND);
        }
        if (dto.getActivationCode().equals(auth.get().getActivationCode())){
            auth.get().setStatus(EStatus.ACTIVE);
            update(auth.get());
            // user service e istek atılacak
            userManager.activateStatus(auth.get().getId());
            return true;
        }else {
            throw new AuthManagerException(ErrorType.ACTIVATE_CODE_ERROR);
        }

    }

    public Boolean updateEmailOrUsername(UpdateEmailOrUsernameRequestDto dto) {

        Optional<Auth> auth=authRepository.findById(dto.getAuthId());
        if (auth.isEmpty()){
            throw  new AuthManagerException(ErrorType.USER_NOT_FOUND);
        }
        auth.get().setUsername(dto.getUsername());
        auth.get().setEmail(dto.getEmail());
        update(auth.get());
        return  true;
    }

    @Transactional
    public Boolean delete(Long id){
        Optional<Auth> auth=findById(id);
        if (auth.isEmpty()){
            throw new AuthManagerException(ErrorType.USER_NOT_FOUND);
        }
        auth.get().setStatus(EStatus.DELETED);
        update(auth.get());

        userManager.delete(id);

        return true;
    }

    @Transactional
    public Boolean delete2(String token){
        Optional<Long> authId=jwtTokenManager.getIdFromToken(token);
        if (authId.isEmpty()){
            throw new AuthManagerException(ErrorType.INVALID_TOKEN);

        }
        Optional<Auth> auth=findById(authId.get());
        if (auth.isEmpty()){
            throw new AuthManagerException(ErrorType.USER_NOT_FOUND);
        }
        auth.get().setStatus(EStatus.DELETED);
        update(auth.get());

        userManager.delete(authId.get());

        return true;
    }


    public List<Long> findByRole(String role) {
        ERole myrole;
        try {
            myrole = ERole.valueOf(role.toUpperCase(Locale.ENGLISH));

        }catch (Exception e){
            throw new AuthManagerException(ErrorType.ROLE_NOT_FOUNDED);
        }
        return authRepository.findAllByRole(myrole).stream().map(x->x.getId()).collect(Collectors.toList());


    }
}
