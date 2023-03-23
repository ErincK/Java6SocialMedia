package com.erinc.service;


import com.erinc.repository.IUserProfileRepository;
import com.erinc.repository.entity.UserProfile;
import com.erinc.utility.ServiceManager;
import org.springframework.stereotype.Service;



@Service
public class UserProfileService extends ServiceManager<UserProfile,String> {

    private final IUserProfileRepository userProfileRepository;


    public UserProfileService(IUserProfileRepository userProfileRepository) {
        super(userProfileRepository);
        this.userProfileRepository = userProfileRepository;

    }



}
