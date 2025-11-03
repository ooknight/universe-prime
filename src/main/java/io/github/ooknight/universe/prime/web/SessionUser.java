package io.github.ooknight.universe.prime.web;

import lombok.Data;

import java.util.Set;

@Data
public class SessionUser {

    public static final String SESSION_NAME = "_su_";

    private Long id;
    private String name;
    private String state;
    private Boolean owner;
    private Set<String> group;
    private Set<String> role;
    private Set<String> permission;
    private Set<String> scope;

}
