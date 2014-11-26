package io.cslinmiso.line.api.impl;

/**
 * 
 * @Package: io.cslinmiso.line.api.impl
 * @FileName: LineApiImpl.java
 * @author: treylin
 * @date: 2014/10/27, 上午 11:37:59
 * 
 * <pre>
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Trey Lin
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

  public static final String EMAIL_REGEX = "[^@]+@[^@]+\\.[^@]+";

  /** The ip. */
  public String ip = "192.168.11.123";

  /** The version. */
  public String version = "3.7.3.82";

  /** The com_name. */
  public String systemName = "LineJ";

  /** The revision. */
  public long revision = 0;

  /** The _headers. */
  public Map<String, String> _headers = new HashMap<String, String>();

  /** The _client. */
  public TalkService.Client _client = null;

  public LineApiImpl() {
    initHeaders("");
  }

  public LineApiImpl(String systemName) {
    initHeaders(systemName);
  }

  
  public static void main(String[] args) {
//    LineApi api = new LineApiImpl();
//    try {
//      api.login("xxxx", "xxxx");
//    } catch (java.net.SocketTimeoutException e) {
//      // setAwaitforVerify false
//    } catch (Exception e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    }
  }
  
  private void initHeaders(String systemName) {
    String osVersion = null;
    String userAgent = null;
    String app = null;
    boolean isMac = true;

    if (isMac) {
      osVersion = "10.9.4-MAVERICKS-x64";
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

    THttpClient transport = new THttpClient(LINE_HTTP_URL);
    transport.setCustomHeaders(_headers);
    transport.open();

    TProtocol protocol = new TCompactProtocol(transport);

    return new TalkService.Client(protocol);
  }

  public LoginResult login(String id, String password) throws Exception {

    IdentityProvider provider = null;
    // Map<String, String> json = null;
    // String sessionKey = null;
    boolean keepLoggedIn = false;
    String accessLocation = this.ip;
    String certificate = "";

    // Login to LINE server.
    if (id.matches(EMAIL_REGEX)) {
      provider = IdentityProvider.LINE; // LINE
      // json = getCertResult(LINE_SESSION_LINE_URL);
    } else {
      provider = IdentityProvider.NAVER_KR; // NAVER
      // json = getCertResult(LINE_SESSION_NAVER_URL);
    }

    throw new Exception("Some codes related to login process has been removed, due to security concers of LINE corporation.");

    // await for pinCode to be certified, it will return a verifier afterward.
    // loginWithVerifier();
  }

  public void loginWithAuthToken(String authToken) throws Exception {
       throw new Exception("Some codes related to login process has been removed, due to security concers of LINE corporation.");
  }

  public AuthQrcode loginWithQrCode() throws Exception {
    // Request QrCode from LINE server.

    throw new Exception("Some codes related to login process has been removed, due to security concers of LINE corporation.");

    // await for QR code to be certified, it will return a verifier afterward.
    // loginWithVerifier();
  }


  public String loginWithVerifier() throws Exception {
       throw new Exception("Some codes related to login process has been removed, due to security concers of LINE corporation.");
  }


  public Map getCertResult(String url) throws Exception {
    Unirest unirest = new Unirest();
    // set timed out in 2 mins.
    unirest.setTimeouts(120000, 120000);
    HttpResponse<JsonNode> jsonResponse = unirest.get(url).headers(this._headers).asJson();
    return Utility.toMap(jsonResponse.getBody().getObject());
  }


  /**
   * find and add Contact by user id
   * 
   * @return
   * @throws TException
   * @throws TalkException
   **/
  public Map<String, Contact> _findAndAddContactsByUserid(int reqSeq, String userid)
      throws TalkException, TException {
    return this._client.findAndAddContactsByUserid(0, userid);
  }

  /**
   * find and add contact by email (not tested)
   * 
   * @return
   * @throws TException
   * @throws TalkException
   **/
  public Map<String, Contact> _findAndAddContactsByEmail(int reqSeq, Set<String> emails)
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
  public Map<String, Contact> _findAndAddContactsByPhone(int reqSeq, Set<String> phone)
      throws TalkException, TException {
    return this._client.findAndAddContactsByPhone(0, phone);
  }

  /**
   * Get profile information
   * 
   * returns Profile object; - picturePath - displayName - phone (base64 encoded?) -
   * allowSearchByUserid - pictureStatus - userid - mid # used for unique id for account -
   * phoneticName - regionCode - allowSearchByEmail - email - statusMessage
   **/
  public Profile _getProfile() throws TalkException, TException {
    return this._client.getProfile();
  }

   public List<String> _getAllContactIds() throws TalkException, TException {
    /** Get all contacts of your LINE account **/
    return this._client.getAllContactIds();
  }

 
  public List<String> _getBlockedContactIds() throws TalkException, TException {
    /** Get all blocked contacts of your LINE account **/
    return this._client.getBlockedContactIds();
  }

    public List<String> _getHiddenContactIds() throws TalkException, TException {
    /** Get all hidden contacts of your LINE account **/
    return this._client.getHiddenContactMids();
  }

  public List<Contact> _getContacts(List<String> ids) throws TalkException, TException {
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

    public Room _createRoom(int reqSeq, List<String> ids) throws TalkException, TException {
    /** Create a chat room **/
    // reqSeq = 0;
    return this._client.createRoom(reqSeq, ids);
  }

    public Room _getRoom(String roomId) throws TalkException, TException {
    /** Get a chat room **/
    return this._client.getRoom(roomId);
  }

    public void _inviteIntoRoom(String roomId, List<String> contactIds) throws TalkException,
      TException {
    /** Invite contacts into room **/
    this._client.inviteIntoRoom(0, roomId, contactIds);
  }

    public void _leaveRoom(String id) throws TalkException, TException {
    /** Leave a chat room **/
    this._client.leaveRoom(0, id);
  }

   public Group _createGroup(int seq, String name, List<String> ids) throws TalkException,
      TException {
    /** Create a group **/
    // seq = 0;
    return this._client.createGroup(seq, name, ids);
  }

   public List<Group> _getGroups(List<String> groupIds) throws TalkException, TException {
    /** Get a list of group with ids **/
    // if type(ids) != list{
    // msg = "argument should be list of group ids"
    // this.raise_error(msg)

    return this._client.getGroups(groupIds);
  }

   public List<String> _getGroupIdsJoined() throws TalkException, TException {
    /** Get group id that you joined **/
    return this._client.getGroupIdsJoined();
  }

  public List<String> _getGroupIdsInvited() throws TalkException, TException {
    /** Get group id that you invited **/
    return this._client.getGroupIdsInvited();
  }

  public void _acceptGroupInvitation(int seq, String groupId) throws TalkException, TException {
    /** Accept a group invitation **/
    // seq = 0;
    this._client.acceptGroupInvitation(seq, groupId);
  }

  public void _cancelGroupInvitation(int seq, String groupId, List<String> contactIds)
      throws TalkException, TException {
    /** Cancel a group invitation **/
    // seq = 0;
    this._client.cancelGroupInvitation(seq, groupId, contactIds);
  }

  public void _inviteIntoGroup(int seq, String groupId, List<String> contactIds)
      throws TalkException, TException {
    /** Invite contacts into group **/
    // seq = 0;
    this._client.inviteIntoGroup(seq, groupId, contactIds);
  }

  public void _leaveGroup(String id) throws TalkException, TException {
    /** Leave a group **/
    this._client.leaveGroup(0, id);
  }

  public List<Message> _getRecentMessages(String id, int count) throws TalkException, TException {
    /** Get recent messages from `id` **/
    return this._client.getRecentMessages(id, count);
  }

  public Message _sendMessage(int seq, Message message) throws TalkException, TException {
    /**
     * Send a message to `id`. `id` could be contact id or group id
     * 
     * param message: `message` instance
     **/
    return this._client.sendMessage(seq, message);
  }

  public long _getLastOpRevision() throws TalkException, TException {
    return this._client.getLastOpRevision();
  }

  public List<Operation> _fetchOperations(long revision, int count) throws TalkException,
      TException {
    return this._client.fetchOperations(revision, count);
  }

  public TMessageBoxWrapUp _getMessageBoxCompactWrapUp(String id) {
    try {
      return this._client.getMessageBoxCompactWrapUp(id);
    } catch (Exception e) {
      return null;
    }
  }

  public TMessageBoxWrapUpResponse _getMessageBoxCompactWrapUpList(int start, int count)
      throws Exception {
    try {
      return this._client.getMessageBoxCompactWrapUpList(start, count);
    } catch (Exception e) {
      throw new Exception(e.getMessage());
    }
  }

  public TalkService.Client getClient() {
    return _client;
  }

  public void setClient(TalkService.Client _client) {
    this._client = _client;
  }

}
