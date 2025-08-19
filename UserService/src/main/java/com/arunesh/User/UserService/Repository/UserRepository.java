package com.arunesh.User.UserService.Repository;

import com.arunesh.User.UserService.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<Users, UUID> {

    @Modifying
    @Query("UPDATE Users u SET u.isActive = :status WHERE u.id IN :ids")
    int bulkUpdateIsActiveByIds(String status, List<UUID> ids);
}
