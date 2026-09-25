package com.tourlab.api.domain.user.entity;

public enum Role {
  USER,
  ADMIN;

  public String authority() {
    return "ROLE_" + name();
  }
}
