package org.tuanna.xcloneserver.modules.role;

import org.tuanna.xcloneserver.modules.role.dtos.RoleDTO;

import java.util.List;
import java.util.UUID;

public interface RoleService {

    List<RoleDTO> findAllByUserId(UUID userId);

}
