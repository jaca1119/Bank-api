package com.itvsme.bank.models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.itvsme.bank.models.account.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
public class UserApp implements UserDetails
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    @JsonIgnore
    private String password;
    private String email;
    private String role;
    @JsonIgnore
    private boolean isEnabled;

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "USER_ID")
    private List<Account> accounts;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return Collections.singleton(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword()
    {
        return this.password;
    }

    @Override
    public String getUsername()
    {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }

    @Override
    public boolean isAccountNonLocked()
    {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getRole()
    {
        return role;
    }

    public void setRole(String role)
    {
        this.role = role;
    }

    @Override
    public boolean isEnabled()
    {
        return isEnabled;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public void setEnabled(boolean enabled)
    {
        isEnabled = enabled;
    }

    public List<Account> getAccounts()
    {
        return accounts;
    }

    public void setAccounts(List<Account> accounts)
    {
        this.accounts = accounts;
    }

    @Override
    public String toString()
    {
        return "UserApp{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", isEnabled=" + isEnabled +
                ", accounts=" + accounts +
                '}';
    }
}
