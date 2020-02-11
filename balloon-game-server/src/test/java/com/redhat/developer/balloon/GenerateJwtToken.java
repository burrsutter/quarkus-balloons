package com.redhat.developer.balloon;

import java.util.HashMap;

import org.eclipse.microprofile.jwt.Claims;

/**
 * GenerateJwtToken
 */
public class GenerateJwtToken {

    /*
     * @param args - 
     * [0]: private key file path
     * [1]: optional name of classpath resource for json document of claims to add; defaults to "/JwtClaims.json"
     * [2]: optional time in seconds for expiration of generated token; defaults to 300
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        
        if(args[0]==null){
            throw new IllegalArgumentException("No private key provided");
        }

        String privateKeyFile = args[0];
        String claimsJson = "/JwtAdminClaims.json";
                   
        HashMap<String, Long> timeClaims = new HashMap<>();
        if (args.length > 1) {
            long duration = Long.parseLong(args[1]);
            long exp = TokenUtils.currentTimeInSecs() + duration;
            timeClaims.put(Claims.exp.name(), exp);
        }
        String token = TokenUtils
        .generateTokenString(privateKeyFile,claimsJson, timeClaims);
        System.out.println(token);
    }
}