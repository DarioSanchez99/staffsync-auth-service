package com.staffsync.auth.infrastructure.adapter.out.persistence.mapper;

import com.staffsync.auth.domain.model.Permission;
import com.staffsync.auth.infrastructure.adapter.out.persistence.entity.PermissionEntity;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {})
public interface PermissionMapper {

    Permission toDomain(PermissionEntity entity);

    PermissionEntity toEntity(Permission domain);

    List<Permission> toDomainList(List<PermissionEntity> entities);

    Set<Permission> toDomainSet(Set<PermissionEntity> entities);
}
