package io.cslinmiso.line.api.impl;

/**
 * 
 * @Package: io.cslinmiso.line.api.impl
 * @FileName: LineApiImpl.java
 * @author: treylin
 * @date: 2016/03/25, 上午 10:46:25
 * 
 * <pre>
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Trey Lin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *  </pre>
 */


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import io.cslinmiso.line.api.LineApi;
import io.cslinmiso.line.model.LoginCallback;
import line.thrift.AuthQrcode;
import line.thrift.Contact;
import line.thrift.Group;
import line.thrift.IdentityProvider;
import line.thrift.LoginResult;
import line.thrift.LoginResultType;
import line.thrift.Message;
import line.thrift.Operation;
import line.thrift.Profile;
import line.thrift.Room;
import line.thrift.TMessageBoxWrapUp;
import line.thrift.TMessageBoxWrapUpResponse;
import line.thrift.TalkException;
import line.thrift.TalkService;
import line.thrift.TalkService.Client;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransportException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LineApiImpl implements LineApi {

  public enum OSType {
    WINDOWS,
    MAC
  }

  private static final String EMAIL_REGEX = "[^@]+@[^@]+\\.[^@]+";

  private static final String X_LINE_ACCESS = "X-Line-Access";

  /** The ip. */
  private String ip = "127.0.0.1";

  /** The line application version. */
  private String version = "4.7.0";

  /** The com_name. */
  private final String systemName;

  private final OSType osType;

  private String id;

  private String password;

  private String authToken;

  private String verifier;

  private String certificate;

  /** The revision. */
  private long revision;

  /** The _client. */
  public TalkService.Client client;

  private final HttpClient httpClient;

  public LineApiImpl(OSType osType, String systemName) {
    this.osType = osType;
    this.systemName = systemName;
    buildHeaders();
    httpClient = HttpClientBuilder.create()
            .setDefaultHeaders(buildHeaders())
            .build();
  }

  public LineApiImpl() {
    this(OSType.MAC, "Line4J");
  }


  public static void main(String[] args) {
    // LineApi api = new LineApiImpl();
    // try {
    // api.login("xxxx", "xxxx");
    // } catch (java.net.SocketTimeoutException e) {
    // // setAwaitforVerify false
    // } catch (Exception e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
  }

  private Collection<Header> buildHeaders() {
    String osVersion;
    String userAgent;
    String app;

    if (osType.equals(OSType.WINDOWS)) {
      osVersion = "6.1.7600-7-x64";
      userAgent = "DESKTOP:WIN:" + osVersion + "(" + version + ")";
      app = "DESKTOPWIN\t" + osVersion + "\tWINDOWS\t" + version;
    } else {
      osVersion = "10.10.4-YOSEMITE-x64";
      userAgent = "DESKTOP:MAC:" + osVersion + "(" + version + ")";
      app = "DESKTOPMAC\t" + osVersion + "\tMAC\t" + version;
    }
    List<Header> headers = new ArrayList<>();

    headers.add(new BasicHeader("User-Agent", userAgent));
    headers.add(new BasicHeader("X-Line-Application", app));

    // The "fetchOperations" doesn't work properly on keep-alive connection
    // If there's the header "Connection: keep-alive", the server won't close the
    // connection, but it won't send response when a new operation is received.
    headers.add(new BasicHeader("Connection", "close"));
    return headers;
  }

  /**
   * Ready.
   * 
   * @throws TTransportException
   */
  public Client ready() throws TTransportException {

    //THttpClient transport = new THttpClient(LINE_HTTP_IN_URL);
    THttpClient transport = new THttpClient(LINE_HTTP_IN_URL, httpClient);
    transport.setCustomHeader(X_LINE_ACCESS, getAuthToken());
    transport.open();

    return new TalkService.Client(new TCompactProtocol(transport));
  }

  @Override
  public LoginResult login(@Nonnull String id, @Nonnull String password) throws Exception {
    return login(id, password, null, null);
  }

  @Override
  public LoginResult login(@Nonnull String id,
                           @Nonnull String password,
                           @Nullable String certificate,
                           @Nullable LoginCallback loginCallback)
          throws Exception {
    this.id = id;
    this.password = password;
    this.certificate = certificate;
    IdentityProvider provider;
    JsonNode certResult;
    String sessionKey;
    boolean keepLoggedIn = true;
    String accessLocation = this.ip;

    // Login to LINE server.
    if (id.matches(EMAIL_REGEX)) {
      provider = IdentityProvider.LINE; // LINE
      certResult = getCertResult(LINE_SESSION_LINE_URL);
    } else {
      provider = IdentityProvider.NAVER_KR; // NAVER
      certResult = getCertResult(LINE_SESSION_NAVER_URL);
    }

    sessionKey = certResult.get("session_key").asText();
    String message =
        (char) (sessionKey.length()) + sessionKey + (char) (id.length()) + id
            + (char) (password.length()) + password;
    String[] keyArr = certResult.get("rsa_key").asText().split(",");
    String keyName = keyArr[0];
    String n = keyArr[1];
    String e = keyArr[2];

    BigInteger modulus = new BigInteger(n, 16);
    BigInteger pubExp = new BigInteger(e, 16);

    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(modulus, pubExp);
    RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(pubKeySpec);
    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
    byte[] enBytes = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
    String encryptString = Hex.encodeHexString(enBytes);

    THttpClient transport = new THttpClient(LINE_HTTP_URL, httpClient);
    transport.open();
    LoginResult result;
    TProtocol protocol = new TCompactProtocol(transport);
    this.client = new TalkService.Client(protocol);

    result = this.client.loginWithIdentityCredentialForCertificate(provider, keyName, encryptString,
                    keepLoggedIn, accessLocation, this.systemName, this.certificate);

    if (result.getType() == LoginResultType.REQUIRE_DEVICE_CONFIRM) {

      setAuthToken(result.getVerifier());

      if (loginCallback != null) {
        loginCallback.onDeviceConfirmRequired(result.getPinCode());
      } else {
        throw new Exception("Device confirmation is required. Please set " +
                LoginCallback.class.getSimpleName() + " to get the pin code");
      }

      // await for pinCode to be certified, it will return a verifier afterward.
      loginWithVerifierForCertificate();
    } else if (result.getType() == LoginResultType.SUCCESS) {
      // if param certificate has passed certification
      setAuthToken(result.getAuthToken());
    }

    // Once the client passed the verification, switch connection to HTTP_IN_URL
    client = ready();
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see api.line.LineApi#loginWithAuthToken(java.lang.String)
   */
  public void loginWithAuthToken(String authToken) throws Exception {
    if (StringUtils.isNotEmpty(authToken)) {
      setAuthToken(authToken);
    }
    THttpClient transport = new THttpClient(LINE_HTTP_URL, httpClient);
    transport.setCustomHeader(X_LINE_ACCESS, getAuthToken());
    transport.open();

    TProtocol protocol = new TCompactProtocol(transport);
    setClient(new TalkService.Client(protocol));
  }

  /*
   * (non-Javadoc)
   * 
   * @see api.line.LineApi#loginWithQrCode()
   */
  public AuthQrcode loginWithQrCode() throws Exception {
    // Request QrCode from LINE server.

    // Map<String, String> json = null;
    boolean keepLoggedIn = false;

    THttpClient transport = new THttpClient(LINE_HTTP_URL, httpClient);
    transport.open();

    TProtocol protocol = new TCompactProtocol(transport);

    this.client = new TalkService.Client(protocol);

    AuthQrcode result = this.client.getAuthQrcode(keepLoggedIn, systemName);

    setAuthToken(result.getVerifier());

    System.out.println("Retrieved QR Code.");

    return result;
    // await for QR code to be certified, it will return a verifier afterward.
    // loginWithVerifier();
  }

  /*
   * (non-Javadoc)
   * 
   * @see api.line.LineApi#loginWithVerifier()
   */
  public String loginWithVerifierForCertificate() throws Exception {
    JsonNode certResult = getCertResult(LINE_CERTIFICATE_URL);

    // login with verifier
    JsonNode resultNode = certResult.get("result");
    String verifierLocal = resultNode.get("verifier").asText();
    this.verifier = verifierLocal;
    LoginResult result = this.client.loginWithVerifierForCertificate(verifierLocal);

    if (result.getType() == LoginResultType.SUCCESS) {
      setAuthToken(result.getAuthToken());
      setCertificate(result.getCertificate());
      return result.getCertificate();
    } else if (result.getType() == LoginResultType.REQUIRE_QRCODE) {
      throw new Exception("require QR code");
    } else {
      throw new Exception("require device confirm");
    }
  }

  private JsonNode getCertResult(String url) throws Exception {
    HttpGet httpGet = new HttpGet(url);
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readTree(httpClient.execute(httpGet).getEntity().getContent());
  }

  public boolean postContent(String url, Map<String, Object> data, InputStream is) throws Exception {
    byte[] byteArray = IOUtils.toByteArray(is);
    Map<String, String> headers = buildHeaders()
            .stream()
            .collect(Collectors.toMap(Header::getName, Header::getValue));
    headers.put(X_LINE_ACCESS, getAuthToken());

    HttpResponse<String> jsonResponse =
        Unirest.post(url)
                .headers(headers)
                .fields(data)
                .field("file", byteArray, "")
                .asString();
    return jsonResponse.getStatus() == 201;
  }

  /**
   * 
   * After login, update authToken to avoid expiration of authToken. This method skip the PinCode
   * validation step.
   * 
   **/
  public boolean updateAuthToken() throws Exception {
    if (this.certificate != null) {
      this.login(this.id, this.password, this.certificate, null);
      this.loginWithAuthToken(this.authToken);
      return true;
    } else {
      throw new Exception("You need to login first. There is no valid certificate");
    }
  }

  /**
   * find and add Contact by user id
   * 
   * @return
   * @throws TException
   * @throws TalkException
   **/
  public Contact findContactByUserid(String userid)
      throws TalkException, TException {
    return this.client.findContactByUserid(userid);
  }
  
  /**
   * find and add Contact by user id
   * 
   * @return
   * @throws TException
   * @throws TalkException
   **/
  public Map<String, Contact> findAndAddContactsByUserid(int reqSeq, String userid)
      throws TalkException, TException {
    return this.client.findAndAddContactsByUserid(0, userid);
  }
  
  /**
   * find contacts by email (not tested)
   * 
   * @return
   * @throws TException
   * @throws TalkException
   **/
  public Map<String, Contact> findContactsByEmail(Set<String> emails)
      throws TalkException, TException {
    return this.client.findContactsByEmail(emails);
  }
  
  /**
   * find and add contact by email (not tested)
   * 
   * @return
   * @throws TException
   * @throws TalkException
   **/
  public Map<String, Contact> findAndAddContactsByEmail(int reqSeq, Set<String> emails)
      throws TalkException, TException {
    return this.client.findAndAddContactsByEmail(0, emails);
  }

  /**
   * find and add contact by phone number (not tested)
   * 
   * @return
   * @return
   * @throws TException
   * @throws TalkException
   **/
  public Map<String, Contact> findContactsByPhone( Set<String> phones)
      throws TalkException, TException {
    return this.client.findContactsByPhone(phones);
  }
  
  /**
   * find and add contact by phone number (not tested)
   * 
   * @return
   * @return
   * @throws TException
   * @throws TalkException
   **/
  public Map<String, Contact> findAndAddContactsByPhone(int reqSeq, Set<String> phones)
      throws TalkException, TException {
    return this.client.findAndAddContactsByPhone(0, phones);
  }

  /**
   * Get profile information
   * 
   * returns Profile object; - picturePath - displayName - phone (base64 encoded?) -
   * allowSearchByUserid - pictureStatus - userid - mid # used for unique id for account -
   * phoneticName - regionCode - allowSearchByEmail - email - statusMessage
   **/
  public Profile getProfile() throws TalkException, TException {
    return this.client.getProfile();
  }

  public List<String> getAllContactIds() throws TalkException, TException {
    /** Get all contacts of your LINE account **/
    return this.client.getAllContactIds();
  }

  public List<String> getBlockedContactIds() throws TalkException, TException {
    /** Get all blocked contacts of your LINE account **/
    return this.client.getBlockedContactIds();
  }

  public List<String> getHiddenContactIds() throws TalkException, TException {
    /** Get all hidden contacts of your LINE account **/
    return this.client.getHiddenContactMids();
  }

  public List<Contact> getContacts(List<String> ids) throws TalkException, TException {
    /**
     * Get contact information list from ids
     * 
     * {returns{ List of Contact list;} - status - capableVideoCall - dispalyName - settings -
     * pictureStatus - capableVoiceCall - capableBuddy - mid - displayNameOverridden - relation -
     * thumbnailUrl_ - createdTime - facoriteTime - capableMyhome - attributes - type - phoneticName
     * - statusMessage
     **/
    // if type(ids) != list{
    // msg = "argument should be list of contact ids"

    return this.client.getContacts(ids);
  }

  public Room createRoom(int reqSeq, List<String> ids) throws TalkException, TException {
    /** Create a chat room **/
    // reqSeq = 0;
    return this.client.createRoom(reqSeq, ids);
  }

  public Room getRoom(String roomId) throws TalkException, TException {
    /** Get a chat room **/
    return this.client.getRoom(roomId);
  }

  public void inviteIntoRoom(String roomId, List<String> contactIds) throws TalkException,
      TException {
    /** Invite contacts into room **/
    this.client.inviteIntoRoom(0, roomId, contactIds);
  }

  public void leaveRoom(String id) throws TalkException, TException {
    /** Leave a chat room **/
    this.client.leaveRoom(0, id);
  }

  public Group createGroup(int seq, String name, List<String> ids) throws TalkException, TException {
    /** Create a group **/
    // seq = 0;
    return this.client.createGroup(seq, name, ids);
  }

  public List<Group> getGroups(List<String> groupIds) throws TalkException, TException {
    /** Get a list of group with ids **/
    // if type(ids) != list{
    // msg = "argument should be list of group ids"
    // this.raise_error(msg)

    return this.client.getGroups(groupIds);
  }

  public List<String> getGroupIdsJoined() throws TalkException, TException {
    /** Get group id that you joined **/
    return this.client.getGroupIdsJoined();
  }

  public List<String> getGroupIdsInvited() throws TalkException, TException {
    /** Get group id that you invited **/
    return this.client.getGroupIdsInvited();
  }

  public void acceptGroupInvitation(int seq, String groupId) throws TalkException, TException {
    /** Accept a group invitation **/
    // seq = 0;
    this.client.acceptGroupInvitation(seq, groupId);
  }

  public void cancelGroupInvitation(int seq, String groupId, List<String> contactIds)
      throws TalkException, TException {
    /** Cancel a group invitation **/
    // seq = 0;
    this.client.cancelGroupInvitation(seq, groupId, contactIds);
  }

  public void inviteIntoGroup(int seq, String groupId, List<String> contactIds)
      throws TalkException, TException {
    /** Invite contacts into group **/
    // seq = 0;
    this.client.inviteIntoGroup(seq, groupId, contactIds);
  }

  public void kickoutFromGroup(int seq, String groupId, List<String> contactIds)
      throws TalkException, TException {
    /** Kick a group members **/
    // seq = 0;
    this.client.kickoutFromGroup(seq, groupId, contactIds);
  }

  public void leaveGroup(String id) throws TalkException, TException {
    /** Leave a group **/
    this.client.leaveGroup(0, id);
  }

  public List<Message> getRecentMessages(String id, int count) throws TalkException, TException {
    /** Get recent messages from `id` **/
    return this.client.getRecentMessages(id, count);
  }

  public Message sendMessage(int seq, Message message) throws TalkException, TException {
    /**
     * Send a message to `id`. `id` could be contact id or group id
     * 
     * param message: `message` instance
     **/
    return this.client.sendMessage(seq, message);
  }

  public long getLastOpRevision() throws TalkException, TException {
    return this.client.getLastOpRevision();
  }

  public List<Operation> fetchOperations(long revision, int count) throws TalkException, TException {
    return this.client.fetchOperations(revision, count);
  }

  public TMessageBoxWrapUp getMessageBoxCompactWrapUp(String id) {
    try {
      return this.client.getMessageBoxCompactWrapUp(id);
    } catch (Exception e) {
      return null;
    }
  }

  public TMessageBoxWrapUpResponse getMessageBoxCompactWrapUpList(int start, int count)
      throws Exception {
    try {
      return this.client.getMessageBoxCompactWrapUpList(start, count);
    } catch (Exception e) {
      throw new Exception(e.getMessage());
    }
  }

  private void setAuthToken(String token) {
    this.authToken = token;
  }

  public TalkService.Client getClient() {
    return client;
  }

  public void setClient(TalkService.Client client) {
    this.client = client;
  }

  public String getAuthToken() {
    return authToken;
  }

  public String getCertificate() {
    return certificate;
  }

  public void setCertificate(String certificate) {
    this.certificate = certificate;
  }

  @Override
  public void close() throws IOException {
    HttpClientUtils.closeQuietly(httpClient);
  }
}
