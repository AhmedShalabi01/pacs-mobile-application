package org.pacs.pacs_mobile_application.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import org.pacs.pacs_mobile_application.R;
import org.pacs.pacs_mobile_application.pojo.requestmodel.LoginModel;
import org.pacs.pacs_mobile_application.pojo.requestmodel.RegistrationModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.AccessAttemptModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.EmployeeAttributesModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.UserInfoModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.VisitorAttributesModel;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Path;

public class BackEndClient {

    private static Context applicationContext;
    private static final String BASE_URL1 = "https://192.168.1.58:8084/login-registration/";
    private static final String BASE_URL2 = "http://192.168.1.58:8086/access-control/";
    private final BackEndApi backEndApi;
    private final BackEndApi backEndApi2;
    private static BackEndClient INSTANCE;

    public BackEndClient(Context context) {
        OkHttpClient.Builder httpClient1 = new OkHttpClient.Builder();
        OkHttpClient.Builder httpClient2 = new OkHttpClient.Builder();
        try {

            InputStream inputStream = context.getResources().openRawResource(R.raw.pacs_system);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) cf.generateCertificate(inputStream);

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("certificate", certificate);

            @SuppressLint("CustomX509TrustManager") TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @SuppressLint("TrustAllX509TrustManager")
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            X509Certificate certificate = chain[0];
                            String serverFingerprint = sha1Fingerprint(certificate);
                            String expectedFingerprint = "03:89:06:68:3F:E1:D2:70:75:C1:4A:EC:B1:45:81:F8:A5:38:D8:59";
                            if (!Objects.requireNonNull(serverFingerprint).equalsIgnoreCase(expectedFingerprint)) {
                                throw new CertificateException("Server certificate is not trusted.");
                            }
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            httpClient1.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0]);
            httpClient1.hostnameVerifier((hostname, session) -> true);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl(BASE_URL1)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient1.build())
                .build();

        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(BASE_URL2)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient2.build())
                .build();

        backEndApi = retrofit1.create(BackEndApi.class);
        backEndApi2 = retrofit2.create(BackEndApi.class);
    }
    private static String sha1Fingerprint(X509Certificate certificate) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] der = certificate.getEncoded();
            md.update(der);
            byte[] digest = md.digest();
            StringBuilder fingerprint = new StringBuilder();
            for (byte b : digest) {
                if (fingerprint.length() > 0) {
                    fingerprint.append(":");
                }
                fingerprint.append(String.format("%02X", b));
            }
            return fingerprint.toString();
        } catch (CertificateEncodingException | NoSuchAlgorithmException e) {
            Log.e("sha1Fingerprint","Failed to get the Fingerprint form the certificate");
            return null;
        }
    }

    public static BackEndClient getINSTANCE(Context context) {
        if (null == INSTANCE){
            INSTANCE = new BackEndClient(context);
        }
        return INSTANCE;
    }

    public Call<EmployeeAttributesModel> registerEmployee(@Body RegistrationModel registrationModel) {
        return backEndApi.registerEmployee(registrationModel);
    }
    public Call<VisitorAttributesModel> registerVisitor(@Body RegistrationModel registrationModel) {
        return backEndApi.registerVisitor(registrationModel);
    }
    public Call<EmployeeAttributesModel> validateEmployee(@Body LoginModel loginModel) {
        return backEndApi.validateEmployee(loginModel);
    }
    public Call<VisitorAttributesModel> validateVisitor(@Body LoginModel loginModel) {
        return backEndApi.validateVisitor(loginModel);
    }
    public Call<UserInfoModel> findEmployeeInfo(@Path("email") String email) {
        return backEndApi.findEmployeeInfo(email);
    }
    public Call<UserInfoModel> findVisitorInfo(@Path("email") String email) {
        return backEndApi.findVisitorInfo(email);
    }

    public Call<List<AccessAttemptModel>>  findEmployeeHistory(@Path("id") String id) {
        return backEndApi2.findEmployeeHistory(id);
    }
    public Call<List<AccessAttemptModel>>  findVisitorHistory(@Path("id") String id) {
        return backEndApi2.findVisitorHistory(id);
    }
}
