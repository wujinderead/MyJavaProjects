package my.projects.jce;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import javax.xml.bind.DatatypeConverter;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.XECPrivateKey;
import java.security.interfaces.XECPublicKey;
import java.security.spec.ECFieldF2m;
import java.security.spec.ECFieldFp;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.NamedParameterSpec;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class EcCurves {
    public static void main(String[] args) throws Exception {
        //testProvider();
        testEcPrime();
        testEcBinary();
        //testXec();
    }

    private static void testProvider() throws Exception {
        KeyFactory factory = KeyFactory.getInstance("EC", "SunEC");
        Provider provider = factory.getProvider();
        Set<Provider.Service> services = provider.getServices();
        @SuppressWarnings("UnstableApiUsage")
        Multimap<String, String> map = MultimapBuilder.hashKeys().hashSetValues().build();

        for (Provider.Service service: services) {
            System.out.println("algorithm: " + service.getAlgorithm());
            System.out.println("classname: " + service.getClassName());
            System.out.println("type: " + service.getType());
            System.out.println("implement: " + service.getAttribute("ImplementedIn"));
            System.out.println("KeySize: " + service.getAttribute("KeySize"));
            System.out.println("SupportedKeyClasses: " + service.getAttribute("SupportedKeyClasses"));
            System.out.println("SupportedCurves: " + service.getAttribute("SupportedCurves"));
            System.out.println();
            map.put(service.getType(), service.getAlgorithm());
        }
        /*
        KeyPairGenerator: [X448, X25519, XDH, EC]
        KeyAgreement: [X448, X25519, ECDH, XDH]
        KeyFactory: [X448, X25519, XDH, EC]
        AlgorithmParameters: [EC]
         */
        for (String key: map.keySet()) {
            System.out.println(key +": " +map.get(key));
        }
    }

    private static void testEcPrime() throws Exception {
        AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC", "SunEC");
        // secp112r1, secp112r2, secp128r1, secp128r2, secp160k1, secp160r1, secp160r2, secp192k1, secp192r1, secp224k1, secp224r1, secp256k1, secp256r1, secp384r1, secp521r1,
        // X9.62 prime192v2, X9.62 prime192v3, X9.62 prime239v1, X9.62 prime239v2, X9.62 prime239v3,
        // sect113r1, sect113r2, sect131r1, sect131r2, sect163k1, sect163r1, sect163r2, sect193r1, sect193r2, sect233k1, sect233r1, sect239k1, sect283k1, sect283r1, sect409k1, sect409r1, sect571k1, sect571r1,
        // X9.62 c2tnb191v1, X9.62 c2tnb191v2, X9.62 c2tnb191v3, X9.62 c2tnb239v1, X9.62 c2tnb239v2, X9.62 c2tnb239v3, X9.62 c2tnb359v1, X9.62 c2tnb431r1,
        // brainpoolP160r1, brainpoolP192r1, brainpoolP224r1, brainpoolP256r1, brainpoolP320r1, brainpoolP384r1, brainpoolP512r1,
        ECGenParameterSpec curveSpec = new ECGenParameterSpec("secp256r1");
        parameters.init(curveSpec);
        ECParameterSpec spec = parameters.getParameterSpec(ECParameterSpec.class);
        System.out.println("A: " + spec.getCurve().getA());
        System.out.println("B: " + spec.getCurve().getB());
        System.out.println("P: " + ((ECFieldFp) spec.getCurve().getField()).getP());
        System.out.println("Si: " + spec.getCurve().getField().getFieldSize());
        System.out.println("Or: " + spec.getOrder());
        System.out.println("Co: " + spec.getCofactor());
        System.out.println("Gx: " + spec.getGenerator().getAffineX());
        System.out.println("Gy: " + spec.getGenerator().getAffineY());
        System.out.println("Sd: " + (spec.getCurve().getSeed() == null ?
                null : DatatypeConverter.printHexBinary(spec.getCurve().getSeed())));

        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC", "SunEC");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        random.setSeed(System.nanoTime());
        generator.initialize(spec, random);
        KeyPair pair = generator.generateKeyPair();
        ECPrivateKey priv = (ECPrivateKey) pair.getPrivate();
        ECPublicKey pub = (ECPublicKey) pair.getPublic();
        System.out.println("algorithm: " + priv.getAlgorithm());
        System.out.println("format: " + priv.getFormat());
        System.out.println("S: " + priv.getS());
        System.out.println("Px: " + pub.getW().getAffineX());
        System.out.println("Py: " + pub.getW().getAffineY());
    }

    private static void testEcBinary() throws Exception {
        AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC", "SunEC");
        // secp112r1, secp112r2, secp128r1, secp128r2, secp160k1, secp160r1, secp160r2, secp192k1, secp192r1, secp224k1, secp224r1, secp256k1, secp256r1, secp384r1, secp521r1,
        // X9.62 prime192v2, X9.62 prime192v3, X9.62 prime239v1, X9.62 prime239v2, X9.62 prime239v3,
        // sect113r1, sect113r2, sect131r1, sect131r2, sect163k1, sect163r1, sect163r2, sect193r1, sect193r2, sect233k1, sect233r1, sect239k1, sect283k1, sect283r1, sect409k1, sect409r1, sect571k1, sect571r1,
        // X9.62 c2tnb191v1, X9.62 c2tnb191v2, X9.62 c2tnb191v3, X9.62 c2tnb239v1, X9.62 c2tnb239v2, X9.62 c2tnb239v3, X9.62 c2tnb359v1, X9.62 c2tnb431r1,
        // brainpoolP160r1, brainpoolP192r1, brainpoolP224r1, brainpoolP256r1, brainpoolP320r1, brainpoolP384r1, brainpoolP512r1,
        ECGenParameterSpec curveSpec = new ECGenParameterSpec("sect571k1");
        parameters.init(curveSpec);
        ECParameterSpec spec = parameters.getParameterSpec(ECParameterSpec.class);
        ECFieldF2m field = (ECFieldF2m) spec.getCurve().getField();
        System.out.println("A: " + spec.getCurve().getA());
        System.out.println("B: " + spec.getCurve().getB());
        System.out.println("size: " + field.getFieldSize());
        System.out.println("M: " + field.getM()); // GF(2^M)
        System.out.println("poly: " + field.getReductionPolynomial().toString(2));
        System.out.println("term: " + Arrays.stream(field.getMidTermsOfReductionPolynomial())
                .boxed().collect(Collectors.toList()));
        System.out.println("Or: " + spec.getOrder());
        System.out.println("Co: " + spec.getCofactor());
        System.out.println("Gx: " + spec.getGenerator().getAffineX());
        System.out.println("Gy: " + spec.getGenerator().getAffineY());
        System.out.println("Sd: " + (spec.getCurve().getSeed() == null ?
                null : DatatypeConverter.printHexBinary(spec.getCurve().getSeed())));
        System.out.println(field.getReductionPolynomial().testBit(field.getM()));
        for (int term: field.getMidTermsOfReductionPolynomial()) {
            System.out.println(field.getReductionPolynomial().testBit(term));
        }
        System.out.println(field.getReductionPolynomial().testBit(0));

        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC", "SunEC");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        random.setSeed(System.nanoTime());
        generator.initialize(spec, random);
        KeyPair pair = generator.generateKeyPair();
        ECPrivateKey priv = (ECPrivateKey) pair.getPrivate();
        ECPublicKey pub = (ECPublicKey) pair.getPublic();
        System.out.println("algorithm: " + priv.getAlgorithm());
        System.out.println("format: " + priv.getFormat());
        System.out.println("S: " + priv.getS());
        System.out.println("Px: " + pub.getW().getAffineX());
        System.out.println("Py: " + pub.getW().getAffineY());
    }

    private static void testXec() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("XDH", "SunEC");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        random.setSeed(System.nanoTime());
        generator.initialize(NamedParameterSpec.X448, random);
        KeyPair pair = generator.generateKeyPair();
        XECPrivateKey priv = (XECPrivateKey) pair.getPrivate();
        XECPublicKey pub = (XECPublicKey) pair.getPublic();
        System.out.println("algorithm: " + priv.getAlgorithm());
        System.out.println("format: " + priv.getFormat());
        System.out.println("S: " + DatatypeConverter.printHexBinary(priv.getScalar().get()));
        System.out.println("U: " + pub.getU());
    }
}
