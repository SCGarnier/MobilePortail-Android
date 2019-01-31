package com.briccsquad.mobileportail.session;

import com.briccsquad.mobileportail.Utils;

import java.nio.charset.StandardCharsets;

public class LoginCredentials {
    private String username;
    private String password;

    public LoginCredentials(String uname, String pwd){
        username = uname;
        password = pwd;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    @Override
    public String toString(){
        String nameAndPwd = username + '_' + password;
        return Utils.calculateMD5(nameAndPwd.getBytes(StandardCharsets.UTF_8));
    }
}
