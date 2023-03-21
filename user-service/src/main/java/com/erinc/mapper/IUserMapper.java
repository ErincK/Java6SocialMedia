package com.erinc.mapper;

import com.erinc.dto.request.NewCreateUserRequestDto;
import com.erinc.dto.request.UpdateEmailOrUsernameRequestDto;
import com.erinc.dto.request.UserProfileUpdateRequestDto;
import com.erinc.rabbitmq.consumer.RegisterConsumer;
import com.erinc.rabbitmq.model.RegisterModel;
import com.erinc.repository.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IUserMapper {


    IUserMapper INSTANCE= Mappers.getMapper(IUserMapper.class);

    UserProfile toUserProfile(final NewCreateUserRequestDto dto);
    UserProfile toUserProfile(final RegisterModel model);
    NewCreateUserRequestDto toNewCreateUserRequestDto(final RegisterModel model);

    UpdateEmailOrUsernameRequestDto toUpdateEmailOrUsernameRequestDto(final UserProfileUpdateRequestDto dto);


}
