package com.itbatia.app.util;

import com.itbatia.app.model.User;
import com.itbatia.app.security.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class Utility {

    public static User getUserFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getUser();
    }

    public static String getFileNameFromLocationInBucket(String locationInBucket) {
        String fileName;
        if (locationInBucket.contains("/")) {
            int lastSlashIndex = locationInBucket.lastIndexOf("/");
            fileName = locationInBucket.substring(lastSlashIndex + 1);
        } else {
            fileName = locationInBucket;
        }
        return fileName;
    }
}
