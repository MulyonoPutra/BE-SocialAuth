package com.bolitik.id.service;

import com.bolitik.id.entity.Role;
import com.bolitik.id.enums.RoleName;
import com.bolitik.id.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class RoleService {

    final
    RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Optional<Role> getByRoleName(RoleName roleName){
        if (roleName == null) {
            return null;
        }
        return roleRepository.findByRoleName(roleName);
    }

    public boolean existsRoleName(RoleName roleName){
        return roleRepository.existsByRoleName(roleName);
    }

    public void save(Role role){
        roleRepository.save(role);
    }
}
