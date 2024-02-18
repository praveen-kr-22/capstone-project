package com.armorcode.capstone.util;

import org.springframework.stereotype.Component;

@Component
public class GetRepoName {
    public String getRepoName(String url) {

        String repoName = "";
        int countSlash = 0;
        int i;
        for(i = 0;i<url.length();i++){
            char ch = url.charAt(i);
            if(ch == '/'){
                countSlash += 1;
            }

            if(countSlash == 5){
                i++;
                break;
            }
        }

        for(;i<url.length();i++){
            char ch = url.charAt(i);

            if(ch == '/'){
                break;
            }

            assert false;
            repoName += ch;
        }



        return repoName;

    }
}
