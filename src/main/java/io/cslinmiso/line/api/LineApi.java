/**
 * @Package: io.cslinmiso.line.api
 * @FileName: LineApi.java
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
 * </pre>
 */

package io.cslinmiso.line.api;

import io.cslinmiso.line.model.LoginCallback;
import line.thrift.AuthQrcode;
import line.thrift.Contact;
import line.thrift.Group;
import line.thrift.LoginResult;
import line.thrift.Message;
import line.thrift.Operation;
import line.thrift.Profile;
import line.thrift.Room;
import line.thrift.TMessageBoxWrapUp;
import line.thrift.TMessageBoxWrapUpResponse;
import line.thrift.TalkException;
import org.apache.thrift.TException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <pre> LineApi, TODO: add Class Javadoc here. </pre>
 * 
 * @author TreyLin
 */
public interface LineApi extends Closeable {

  /**
   * The Constant LINE_DOMAIN.
   * 
   * http://gd2.line.naver.jp, http://gd2u.line.naver.jp are also work.
   * 
   **/
  public static final String LINE_DOMAIN = "http://ga2.line.naver.jp";

  /** The Constant LINE_HTTP_URL. */
  public static final String LINE_HTTP_URL = LINE_DOMAIN + "/api/v4/TalkService.do";

  /**
   * The Constant LINE_HTTP_IN_URL.
   * 
   * AKA LINE_POLL_PATH in purple-line
   * http://altrepo.eu/git/purple-line/commit/0ac35486e9f2ca6266c461b59536a29fe440a565.diff
   * */
  public static final String LINE_HTTP_IN_URL = LINE_DOMAIN + "/P4";

  public static final String LINE_NORMAL_LONGPOLL_URL = LINE_DOMAIN + "/NP4";
  
  public static final String LINE_COMPACT_MESSAGE_URL = LINE_DOMAIN + "/C5";
  
  /** The Constant LINE_COMMAND_URL. */
  public static final String LINE_COMMAND_URL = LINE_DOMAIN + "/S4";

  /** The Constant LINE_CERTIFICATE_URL. */
  public static final String LINE_CERTIFICATE_URL = LINE_DOMAIN + "/Q";

  /** The Constant LINE_SHOP_URL. */
  public static final String LINE_SHOP_URL = LINE_DOMAIN + "/SHOP4";

  /** 
   * The Constant LINE_QRCODE_PREFIX_URL.
   *  which only available from LINE app on mobile device.
   *  "line://au/q/" combine with verifier from AuthQrcode will be the complete url. 
   **/
  public static final String LINE_QRCODE_PREFIX_URL = "line://au/q/";
  
  /** The Constant LINE_SESSION_LINE_URL. */
  public static final String LINE_SESSION_LINE_URL = LINE_DOMAIN + "/authct/v1/keys/line";

  /** The Constant LINE_SESSION_NAVER_URL. */
  public static final String LINE_SESSION_NAVER_URL = LINE_DOMAIN + "/authct/v1/keys/naver";

  public static final String LINE_PROFILE_URL = "http://dl.profile.line.naver.jp";

  public static final String LINE_OBJECT_STORAGE_URL = "http://os.line.naver.jp/os/m/";

  public static final String LINE_UPLOADING_URL = "https://os.line.naver.jp/talk/m/upload.nhn";

  public static final String LINE_STICKER_URL = "http://dl.stickershop.line.naver.jp/products/0/0/";

  /** The Constant DATE_PATTERN. */
  public static final String DATE_PATTERN = "yyyyMMdd";

  LoginResult login(@Nonnull String id, @Nonnull String password) throws Exception;

  LoginResult login(@Nonnull String id,
                    @Nonnull String password,
                    @Nullable String certificate,
                    @Nullable LoginCallback loginCallback) throws Exception;

  boolean updateAuthToken() throws Exception;

  /*
   * (non-Javadoc)
   * 
   * @see io.cslinmiso.line.api.LineApi#loginWithAuthToken(java.lang.String)
   */
  void loginWithAuthToken(String authToken) throws Exception;

  /*
   * (non-Javadoc)
   * 
   * @see io.cslinmiso.line.api.LineApi#loginWithQrCode()
   */
  AuthQrcode loginWithQrCode() throws Exception;

  /*
   * (non-Javadoc)
   * 
   * @see io.cslinmiso.line.api.LineApi#loginWithVerifier()
   */
  String loginWithVerifierForCertificate() throws Exception;

  boolean postContent(String url, Map<String, Object> data, InputStream is) throws Exception;

  /*
   * (non-Javadoc)
   * 
   * @see io.cslinmiso.line.api.LineApi#findContactByUserid(java.lang.String)
   */
  Contact findContactByUserid(String userid) throws TalkException, TException;

  /*
   * (non-Javadoc)
   * 
   * @see io.cslinmiso.line.api.LineApi#findAndAddContactsByUserid(int, java.lang.String)
   */
  Map<String, Contact> findAndAddContactsByUserid(int reqSeq, String userid) throws TalkException,
      TException;

  /*
   * (non-Javadoc)
   * 
   * @see io.cslinmiso.line.api.LineApi#findContactsByEmail(java.util.Set)
   */
  public Map<String, Contact> findContactsByEmail(Set<String> emails) throws TalkException,
      TException;

  /*
   * (non-Javadoc)
   * 
   * @see io.cslinmiso.line.api.LineApi#_findAndAddContactsByEmail(int, java.util.Set)
   */
  Map<String, Contact> findAndAddContactsByEmail(int reqSeq, Set<String> emails)
      throws TalkException, TException;

  /*
   * (non-Javadoc)
   * 
   * @see io.cslinmiso.line.api.LineApi#findContactsByPhone(java.util.Set)
   */
  public Map<String, Contact> findContactsByPhone(Set<String> phones) throws TalkException,
      TException;

  /*
   * (non-Javadoc)
   * 
   * @see io.cslinmiso.line.api.LineApi#_findAndAddContactsByPhone(int, java.util.Set)
   */
  Map<String, Contact> findAndAddContactsByPhone(int reqSeq, Set<String> phone)
      throws TalkException, TException;

  /*
   * (non-Javadoc)
   * 
   * @see io.cslinmiso.line.api.LineApi#_getProfile()
   */
  Profile getProfile() throws TalkException, TException;

  /*
   * (non-Javadoc)
   * 
   * @see io.cslinmiso.line.api.LineApi#_getAllContactIds()
   */
  List<String> getAllContactIds() throws TalkException, TException;

  /*
   * (non-Javadoc)
   * 
   * @see io.cslinmiso.line.api.LineApi#_getBlockedContactIds()
   */
  List<String> getBlockedContactIds() throws TalkException, TException;

  /*
   * (non-Javadoc)
   * 
   * @see io.cslinmiso.line.api.LineApi#_getHiddenContactIds()
   */
  List<String> getHiddenContactIds() throws TalkException, TException;

  /*
   * (non-Javadoc)
   * 
   * @see io.cslinmiso.line.api.LineApi#_getContacts(java.util.List)
   */
  List<Contact> getContacts(List<String> ids) throws TalkException, TException;

  /*
   * (non-Javadoc)
   * 
   * @see io.cslinmiso.line.api.LineApi#_createRoom(int, java.util.List)
   */
  Room createRoom(int reqSeq, List<String> ids) throws TalkException, TException;

  /*
   * (non-Javadoc)
   * 
   * @see io.cslinmiso.line.api.LineApi#_getRoom(java.lang.String)
   */
  Room getRoom(String roomId) throws TalkException, TException;

  /*
   * (non-Javadoc)
   * 
   * @see io.cslinmiso.line.api.LineApi#_inviteIntoRoom(java.lang.String, java.util.List)
   */
  void inviteIntoRoom(String roomId, List<String> contactIds) throws TalkException, TException;

  /*
   * (non-Javadoc)
   * 
   * @see io.cslinmiso.line.api.LineApi#_leaveRoom(java.lang.String)
   */
  void leaveRoom(String id) throws TalkException, TException;

  /*
   * (non-Javadoc)
   * 
   * @see io.cslinmiso.line.api.LineApi#_createGroup(int, java.lang.String, java.util.List)
   */
  Group createGroup(int seq, String name, List<String> ids) throws TalkException, TException;

  /*
   * (non-Javadoc)
   * 
   * @see io.cslinmiso.line.api.LineApi#_getGroups(java.util.List)
   */
  List<Group> getGroups(List<String> groupIds) throws TalkException, TException;

  /*
   * (non-Javadoc)
   * 
   * @see io.cslinmiso.line.api.LineApi#_getGroupIdsJoined()
   */
  List<String> getGroupIdsJoined() throws TalkException, TException;

  /*
   * (non-Javadoc)
   * 
   * @see io.cslinmiso.line.api.LineApi#_getGroupIdsInvited()
   */
  List<String> getGroupIdsInvited() throws TalkException, TException;

  /*
   * (non-Javadoc)
   * 
   * @see io.cslinmiso.line.api.LineApi#_acceptGroupInvitation(int, java.lang.String)
   */
  void acceptGroupInvitation(int seq, String groupId) throws TalkException, TException;

  /*
   * (non-Javadoc)
   * 
   * @see io.cslinmiso.line.api.LineApi#_cancelGroupInvitation(int, java.lang.String,
   * java.util.List)
   */
  void cancelGroupInvitation(int seq, String groupId, List<String> contactIds)
      throws TalkException, TException;

  void kickoutFromGroup(int seq, String groupId, List<String> contactIds) throws TalkException,
      TException;

  /*
   * (non-Javadoc)
   * 
   * @see io.cslinmiso.line.api.LineApi#_inviteIntoGroup(int, java.lang.String, java.util.List)
   */
  void inviteIntoGroup(int seq, String groupId, List<String> contactIds) throws TalkException,
      TException;

  /*
   * (non-Javadoc)
   * 
   * @see io.cslinmiso.line.api.LineApi#_leaveGroup(java.lang.String)
   */
  void leaveGroup(String id) throws TalkException, TException;

  /*
   * (non-Javadoc)
   * 
   * @see io.cslinmiso.line.api.LineApi#_getRecentMessages(java.lang.String, int)
   */
  List<Message> getRecentMessages(String id, int count) throws TalkException, TException;

  /*
   * (non-Javadoc)
   * 
   * @see io.cslinmiso.line.api.LineApi#_sendMessage(int, thrift.Message)
   */
  Message sendMessage(int seq, Message message) throws TalkException, TException;

  /*
   * (non-Javadoc)
   * 
   * @see io.cslinmiso.line.api.LineApi#_getLastOpRevision()
   */
  long getLastOpRevision() throws TalkException, TException;

  /*
   * (non-Javadoc)
   * 
   * @see io.cslinmiso.line.api.LineApi#_fetchOperations(long, int)
   */
  List<Operation> fetchOperations(long revision, int count) throws TalkException, TException;

  /*
   * (non-Javadoc)
   * 
   * @see io.cslinmiso.line.api.LineApi#_getMessageBoxCompactWrapUp(java.lang.String)
   */
  TMessageBoxWrapUp getMessageBoxCompactWrapUp(String id);

  /*
   * (non-Javadoc)
   * 
   * @see io.cslinmiso.line.api.LineApi#_getMessageBoxCompactWrapUpList(int, int)
   */
  TMessageBoxWrapUpResponse getMessageBoxCompactWrapUpList(int start, int count) throws Exception;

  String getAuthToken();

  String getCertificate();

  void close() throws IOException;
}
