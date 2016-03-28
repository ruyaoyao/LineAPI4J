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


import io.cslinmiso.line.api.LineApi;
import io.cslinmiso.line.utils.Utility;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.Cipher;

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
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransportException;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

public class LineApiImpl implements LineApi {

  private static final String EMAIL_REGEX = "[^@]+@[^@]+\\.[^@]+";

  private static final String CERT_FILE = "./line.crt";

  /** The ip. */
  private String ip = "127.0.0.1";

  /** The version. */
  private String version = "4.0.1";

  /** The com_name. */
  private String systemName = "Line4J";

  private String id;

  private String password;

  private String authToken;

  private String verifier;

  private String certificate;

  /** The revision. */
  private long revision = 0;

  /** The _headers. */
  private Map<String, String> _headers = new HashMap<String, String>();

  /** The _client. */
  public TalkService.Client _client = null;

  public LineApiImpl() {
    initHeaders("");
  }

  public LineApiImpl(String systemName) {
    initHeaders(systemName);
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

  private void initHeaders(String systemName) {
    String osVersion = null;
    String userAgent = null;
    String app = null;
    boolean isMac = true;

    if (isMac) {
      osVersion = "10.10.4-YOSEMITE-x64";
      userAgent = "DESKTOP:MAC:" + osVersion + "(" + version + ")";
      app = "DESKTOPMAC\t" + osVersion + "\tMAC\t" + version;
    } else {
      osVersion = "6.1.7600-7-x64";
      userAgent = "DESKTOP:WIN:" + osVersion + "(" + version + ")";
      app = "DESKTOPWIN\t" + osVersion + "\tWINDOWS\t" + version;
    }

    if (StringUtils.isNotEmpty(systemName)) {
      this.systemName = systemName;
    }

    _headers.put("User-Agent", userAgent);
    _headers.put("X-Line-Application", app);
  }

  /**
   * Ready.
   * 
   * @return the talk service. client
   * @throws TTransportException
   */
  public Client ready() throws TTransportException {

    THttpClient transport = new THttpClient(LINE_HTTP_IN_URL);
    transport.setCustomHeaders(_headers);
    transport.open();

    TProtocol protocol = new TCompactProtocol(transport);

    return new TalkService.Client(protocol);
  }

  public LoginResult login(String id, String password) throws Exception {
    return login(id, password, null);
  }

  public LoginResult login(String id, String password, String certificate) throws Exception {

    IdentityProvider provider = null;
    Map<String, String> json = null;
    String sessionKey = null;
    boolean keepLoggedIn = true;
    String accessLocation = this.ip;

    // Login to LINE server.
    if (id.matches(EMAIL_REGEX)) {
      provider = IdentityProvider.LINE; // LINE
      json = getCertResult(LINE_SESSION_LINE_URL);
    } else {
      provider = IdentityProvider.NAVER_KR; // NAVER
      json = getCertResult(LINE_SESSION_NAVER_URL);
    }

    if (id != null) {
      this.id = id;
    }

    if (password != null) {
      this.password = password;
    }

    if (StringUtils.isNotEmpty(certificate)) {
      setCertificate(certificate);
    } else {
      // read the certificate file if it exists
      try {
        List<String> readFile = Utility.readFile(LineApiImpl.CERT_FILE);
        String tmpCert = readFile != null ? readFile.get(0) : "";
        if (tmpCert != null) {
          setCertificate(tmpCert);
        }
      } catch (Exception ex) {
        setCertificate("");
      }
    }

    sessionKey = json.get("session_key");
    String tmpMsg =
        (char) (sessionKey.length()) + sessionKey + (char) (id.length()) + id
            + (char) (password.length()) + password;
    String message = new String(tmpMsg.getBytes(), java.nio.charset.StandardCharsets.UTF_8);
    String[] keyArr = json.get("rsa_key").split(",");
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
    byte[] enBytes = cipher.doFinal(message.getBytes());
    String encryptString = Hex.encodeHexString(enBytes);

    THttpClient transport = new THttpClient(LINE_HTTP_URL);
    transport.setCustomHeaders(_headers);
    transport.open();

    TProtocol protocol = new TCompactProtocol(transport);
    this._client = new TalkService.Client(protocol);

    LoginResult result =
        this._client.loginWithIdentityCredentialForCertificate(provider, keyName, encryptString,
            keepLoggedIn, accessLocation, this.systemName, this.certificate);

    if (result.getType() == LoginResultType.REQUIRE_DEVICE_CONFIRM) {

      _headers.put("X-Line-Access", result.getVerifier());
      String pinCode = result.getPinCode();

      System.out.printf("Enter PinCode '%s' to your mobile phone in 2 minutes.\n", pinCode);
      // await for pinCode to be certified, it will return a verifier afterward.
      loginWithVerifierForCertificate();
    } else if (result.getType() == LoginResultType.SUCCESS) {
      // if param certificate has passed certification
      setAuthToken(result.getAuthToken());
    }

    // Once the client passed the verification, switch connection to HTTP_IN_URL
    this._client = ready();
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
    THttpClient transport = new THttpClient(LINE_HTTP_URL);
    transport.setCustomHeaders(_headers);
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

    THttpClient transport = new THttpClient(LINE_HTTP_URL);
    transport.setCustomHeaders(_headers);
    transport.open();

    TProtocol protocol = new TCompactProtocol(transport);

    this._client = new TalkService.Client(protocol);

    AuthQrcode result = this._client.getAuthQrcode(keepLoggedIn, systemName);

    _headers.put("X-Line-Access", result.getVerifier());

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
    Map json = null;
    json = getCertResult(LINE_CERTIFICATE_URL);
    if (json == null) {
      throw new Exception("fail to pass certificate check.");
    }

    // login with verifier
    json = (Map) json.get("result");
    String verifier = (String) json.get("verifier");
    this.verifier = verifier;
    LoginResult result = this._client.loginWithVerifierForCertificate(verifier);

    if (result.getType() == LoginResultType.SUCCESS) {
      setAuthToken(result.getAuthToken());
      String tmpCert = result.getCertificate();
      if (tmpCert != null) {
        Utility.writeFile(IOUtils.toInputStream(tmpCert), this.CERT_FILE);
        setCertificate(tmpCert);
      }
      return tmpCert;
    } else if (result.getType() == LoginResultType.REQUIRE_QRCODE) {
      throw new Exception("require QR code");
    } else {
      throw new Exception("require device confirm");
    }
  }

  public Map getCertResult(String url) throws Exception {
    Unirest unirest = new Unirest();
    // set timed out in 2 mins.
    Unirest.setTimeouts(120000, 120000);
    HttpResponse<JsonNode> jsonResponse = unirest.get(url).headers(this._headers).asJson();
    return Utility.toMap(jsonResponse.getBody().getObject());
  }

  public boolean postContent(String url, Map<String, Object> data, InputStream is) throws Exception {
    Unirest unirest = new Unirest();
    byte[] byteArray = IOUtils.toByteArray(is);
    HttpResponse<JsonNode> jsonResponse =
        unirest.post(url).headers(this._headers).fields(data).field("file", byteArray, "").asJson();
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
      this.login(this.id, this.password);
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
    return this._client.findContactByUserid(userid);
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
    return this._client.findAndAddContactsByUserid(0, userid);
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
    return this._client.findContactsByEmail(emails);
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
    return this._client.findAndAddContactsByEmail(0, emails);
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
    return this._client.findContactsByPhone(phones);
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
    return this._client.findAndAddContactsByPhone(0, phones);
  }

  /**
   * Get profile information
   * 
   * returns Profile object; - picturePath - displayName - phone (base64 encoded?) -
   * allowSearchByUserid - pictureStatus - userid - mid # used for unique id for account -
   * phoneticName - regionCode - allowSearchByEmail - email - statusMessage
   **/
  public Profile getProfile() throws TalkException, TException {
    return this._client.getProfile();
  }

  public List<String> getAllContactIds() throws TalkException, TException {
    /** Get all contacts of your LINE account **/
    return this._client.getAllContactIds();
  }

  public List<String> getBlockedContactIds() throws TalkException, TException {
    /** Get all blocked contacts of your LINE account **/
    return this._client.getBlockedContactIds();
  }

  public List<String> getHiddenContactIds() throws TalkException, TException {
    /** Get all hidden contacts of your LINE account **/
    return this._client.getHiddenContactMids();
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

    return this._client.getContacts(ids);
  }

  public Room createRoom(int reqSeq, List<String> ids) throws TalkException, TException {
    /** Create a chat room **/
    // reqSeq = 0;
    return this._client.createRoom(reqSeq, ids);
  }

  public Room getRoom(String roomId) throws TalkException, TException {
    /** Get a chat room **/
    return this._client.getRoom(roomId);
  }

  public void inviteIntoRoom(String roomId, List<String> contactIds) throws TalkException,
      TException {
    /** Invite contacts into room **/
    this._client.inviteIntoRoom(0, roomId, contactIds);
  }

  public void leaveRoom(String id) throws TalkException, TException {
    /** Leave a chat room **/
    this._client.leaveRoom(0, id);
  }

  public Group createGroup(int seq, String name, List<String> ids) throws TalkException, TException {
    /** Create a group **/
    // seq = 0;
    return this._client.createGroup(seq, name, ids);
  }

  public List<Group> getGroups(List<String> groupIds) throws TalkException, TException {
    /** Get a list of group with ids **/
    // if type(ids) != list{
    // msg = "argument should be list of group ids"
    // this.raise_error(msg)

    return this._client.getGroups(groupIds);
  }

  public List<String> getGroupIdsJoined() throws TalkException, TException {
    /** Get group id that you joined **/
    return this._client.getGroupIdsJoined();
  }

  public List<String> getGroupIdsInvited() throws TalkException, TException {
    /** Get group id that you invited **/
    return this._client.getGroupIdsInvited();
  }

  public void acceptGroupInvitation(int seq, String groupId) throws TalkException, TException {
    /** Accept a group invitation **/
    // seq = 0;
    this._client.acceptGroupInvitation(seq, groupId);
  }

  public void cancelGroupInvitation(int seq, String groupId, List<String> contactIds)
      throws TalkException, TException {
    /** Cancel a group invitation **/
    // seq = 0;
    this._client.cancelGroupInvitation(seq, groupId, contactIds);
  }

  public void inviteIntoGroup(int seq, String groupId, List<String> contactIds)
      throws TalkException, TException {
    /** Invite contacts into group **/
    // seq = 0;
    this._client.inviteIntoGroup(seq, groupId, contactIds);
  }

  public void kickoutFromGroup(int seq, String groupId, List<String> contactIds)
      throws TalkException, TException {
    /** Kick a group members **/
    // seq = 0;
    this._client.kickoutFromGroup(seq, groupId, contactIds);
  }

  public void leaveGroup(String id) throws TalkException, TException {
    /** Leave a group **/
    this._client.leaveGroup(0, id);
  }

  public List<Message> getRecentMessages(String id, int count) throws TalkException, TException {
    /** Get recent messages from `id` **/
    return this._client.getRecentMessages(id, count);
  }

  public Message sendMessage(int seq, Message message) throws TalkException, TException {
    /**
     * Send a message to `id`. `id` could be contact id or group id
     * 
     * param message: `message` instance
     **/
    return this._client.sendMessage(seq, message);
  }

  public long getLastOpRevision() throws TalkException, TException {
    return this._client.getLastOpRevision();
  }

  public List<Operation> fetchOperations(long revision, int count) throws TalkException, TException {
    return this._client.fetchOperations(revision, count);
  }

  public TMessageBoxWrapUp getMessageBoxCompactWrapUp(String id) {
    try {
      return this._client.getMessageBoxCompactWrapUp(id);
    } catch (Exception e) {
      return null;
    }
  }

  public TMessageBoxWrapUpResponse getMessageBoxCompactWrapUpList(int start, int count)
      throws Exception {
    try {
      return this._client.getMessageBoxCompactWrapUpList(start, count);
    } catch (Exception e) {
      throw new Exception(e.getMessage());
    }
  }

  private void setAuthToken(String token) {
    _headers.put("X-Line-Access", token);
    this.authToken = token;
  }

  public TalkService.Client getClient() {
    return _client;
  }

  public void setClient(TalkService.Client _client) {
    this._client = _client;
  }

  public String getLineAccessToken() {
    return _headers.get("X-Line-Access");
  }

  public String getCertificate() {
    return certificate;
  }

  public void setCertificate(String certificate) {
    this.certificate = certificate;
  }

}
