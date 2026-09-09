package com.staffsync.auth.infrastructure.adapter.out.persistence.mapper;

import com.staffsync.auth.domain.model.User;
import com.staffsync.auth.infrastructure.adapter.out.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {

    User toDomain(UserEntity entity);

    @Mapping(target = "id", source = "id")
    UserEntity toEntity(User domain);

    List<User> toDomainList(List<UserEntity> entities);
}
