package com.erinc.mapper;

import com.erinc.dto.request.NewCreateUserRequestDto;
import com.erinc.dto.request.RegisterRequestDto;
import com.erinc.dto.response.RegisterResponseDto;
import com.erinc.rabbitmq.model.RegisterMailModel;
import com.erinc.rabbitmq.model.RegisterModel;
import com.erinc.repository.entity.Auth;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IAuthMapper {


    IAuthMapper INSTANCE= Mappers.getMapper(IAuthMapper.class);
    Auth toAuth(final RegisterRequestDto dto);
    RegisterResponseDto toRegisterResponseDto(final Auth auth);
    @Mapping(source = "id",target = "authId")
    NewCreateUserRequestDto toNewCreateUserRequestDto(final Auth auth);

    @Mapping(source = "id",target = "authId")
    RegisterModel toRegisterModel(final Auth auth);

    RegisterMailModel toRegisterMailModel(final Auth auth);

}
