package com.innowise.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class User {
    private String id;
    private String email;
    private String emailConstraint;
    private boolean emailVerified;
    private boolean enabled;
    private String federationLink;
    private String firstName;
    private String lastName;
    private String realmId;
    private String username;
    private long createdTimestamp;
    private String serviceAccountClientLink;
    private int notBefore;
}
