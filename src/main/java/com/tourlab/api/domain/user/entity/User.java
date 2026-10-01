package com.tourlab.api.domain.user.entity;

import com.tourlab.api.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
    name = "users",
    uniqueConstraints = @UniqueConstraint(name = "uk_users_kakao_id", columnNames = "kakao_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 255)
  private String email;

  @Column(length = 60)
  private String password;

  @Column(name = "kakao_id")
  private Long kakaoId;

  @Column(nullable = false, length = 20)
  private String nickname;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private Role role;

  public User(String email, String password, String nickname) {
    this.email = email;
    this.password = password;
    this.nickname = nickname;
    this.role = Role.USER;
  }

  public User(Long kakaoId) {
    this.kakaoId = kakaoId;
    this.nickname = "카카오 사용자";
    this.role = Role.USER;
  }

  public void updateNickname(String nickname) {
    this.nickname = nickname;
  }

  public void changePassword(String encodedPassword) {
    this.password = encodedPassword;
  }
}
