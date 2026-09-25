package com.tourlab.api.global.security;

import com.tourlab.api.domain.user.entity.Role;
import java.util.Collection;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record UserPrincipal(Long id, Role role) implements UserDetails {

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(role.authority()));
  }

  @Override
  public @Nullable String getPassword() {
    return "";
  }

  @Override
  public String getUsername() {
    return "";
  }
}
