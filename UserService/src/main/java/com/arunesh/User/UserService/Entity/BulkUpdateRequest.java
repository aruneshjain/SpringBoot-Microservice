package com.arunesh.User.UserService.Entity;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class BulkUpdateRequest {
    private String status;
    private List<UUID> ids;
}
