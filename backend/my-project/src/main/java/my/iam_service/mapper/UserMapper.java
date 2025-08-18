package my.iam_service.mapper;

import my.iam_service.model.dto.role.RoleDTO;
import my.iam_service.model.dto.user.UserDTO;
import my.iam_service.model.dto.user.UserProfileDTO;
import my.iam_service.model.dto.user.UserSearchDTO;
import my.iam_service.model.entity.Role;
import my.iam_service.model.entity.User;
import my.iam_service.model.enums.RegistrationStatus;
import my.iam_service.model.request.user.NewUserRequest;
import my.iam_service.model.request.user.RegistrationUserRequest;
import my.iam_service.model.request.user.UpdateUserRequest;
import org.hibernate.type.descriptor.DateTimeUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        imports = {RegistrationStatus.class, Objects.class, DateTimeUtils.class}
)
public interface UserMapper {

    @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
    UserDTO toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "registrationStatus", expression = "java(RegistrationStatus.ACTIVE)")
    User createUser(NewUserRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "updated", expression = "java(java.time.LocalDateTime.now())")
    void updateUser(@MappingTarget User user, UpdateUserRequest request);

    @Mapping(source = "deleted", target = "isDeleted")
    @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
    UserSearchDTO toUserSearchDto(User user);

    @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "token", source = "token")
    @Mapping(target = "refreshToken", source = "refreshToken")
    UserProfileDTO toUserProfileDto(User user, String token, String refreshToken);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "registrationStatus", expression = "java(RegistrationStatus.PENDING_CONFIRMATION)")
    User fromDto(RegistrationUserRequest request);

    default List<RoleDTO> mapRoles(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new RoleDTO(role.getId(), role.getName()))
                .toList();
    }

}
