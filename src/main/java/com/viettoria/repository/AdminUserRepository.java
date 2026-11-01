package com.viettoria.repository;

import com.viettoria.entity.AdminUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUserEntity, String> {
    Optional<AdminUserEntity> findByUsername(String username);
}
