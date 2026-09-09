package com.staffsync.auth.infrastructure.adapter.out.persistence.mapper;

import com.staffsync.auth.domain.model.Role;
import com.staffsync.auth.infrastructure.adapter.out.persistence.entity.RoleEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PermissionMapper.class})
public interface RoleMapper {

    Role toDomain(RoleEntity entity);

    RoleEntity toEntity(Role domain);

    List<Role> toDomainList(List<RoleEntity> entities);
}
