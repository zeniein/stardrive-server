package cn.zeniein.stardrive.support.jwt;

import cn.zeniein.stardrive.common.ResponseEnum;
import cn.zeniein.stardrive.exception.BizException;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;

import java.text.ParseException;
import java.util.Date;

public class JwtUtils {

    private static final String ISSUER = "stardrive";

    private static final int EXPIRATION = 1000 * 60 * 60 * 4;

    private static final String SECRET_KEY = "sjdakldjskljdskljdklsjdklsjdksajkdljaskldjldksajkldjsakldjklsajsjasd";

    /**
     * 生成JWT
     * @param userId 用户ID
     * @param role 用户角色
     * @return jwt
     */
    public static String generate(String userId, String role) throws JOSEException {
        Date now = new Date();
        Date expirationTime = new Date(now.getTime() + EXPIRATION);

        JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder();
        claimsBuilder.notBeforeTime(now);

        claimsBuilder.expirationTime(expirationTime);
        claimsBuilder.issuer(ISSUER);
        claimsBuilder.claim("uid", userId);
        claimsBuilder.claim("role", role);
        JWTClaimsSet claimsSet = claimsBuilder.build();

        JWSSigner signer = new MACSigner(SECRET_KEY);
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256)
                .type(JOSEObjectType.JWT)
                .build();
        Payload payload = new Payload(claimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);
        jwsObject.sign(signer);

        return jwsObject.serialize();
    }

    public static JWTClaimsSet verify(String jwt) throws ParseException, JOSEException {
        JWSObject jwsObject = JWSObject.parse(jwt);
        Payload payload = jwsObject.getPayload();


        JWSVerifier verifier = new MACVerifier(SECRET_KEY);
        boolean isValid = jwsObject.verify(verifier);
        if(!isValid) {
            throw new BizException(ResponseEnum.ERROR.getStatus(), "验证token失败");
        }
        JWTClaimsSet claimsSet = JWTClaimsSet.parse(payload.toJSONObject());
        Date now = new Date();
        if(now.before(claimsSet.getNotBeforeTime())) {
            throw new BizException(ResponseEnum.ERROR.getStatus(), "JWT尚未生效");
        } else if(now.after(claimsSet.getExpirationTime())) {
            throw new BizException(ResponseEnum.ERROR.getStatus(), "JWT已过期");
        }


        return claimsSet;
    }


}
