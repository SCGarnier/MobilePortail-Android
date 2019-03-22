package com.briccsquad.mobileportail.session;

import android.support.annotation.NonNull;

import com.briccsquad.mobileportail.Utils;

import java.nio.charset.StandardCharsets;

/**
 * A class representing the login credentials - that is, the combination
 * of a username and a password - of a certain user.
 */
public class LoginCredentials {
    private final String username;
    private final String password;

    public LoginCredentials(@NonNull String uname, @NonNull String pwd){
        username = uname;
        password = pwd;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    /**
     * Convert the username and password into a unique identifier, which is
     * the username and MD5 sum of the password separated by an underscore.
     * Safe for filenames.
     * @return The unique identifier.
     */
    @NonNull
    @Override
    public String toString(){
        String nameAndPwd = username + '_' + password;
        return Utils.calculateMD5(nameAndPwd.getBytes(StandardCharsets.UTF_8));
    }
}
