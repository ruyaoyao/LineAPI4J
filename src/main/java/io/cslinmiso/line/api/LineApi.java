/**
 * @Package:  io.cslinmiso.line.api
 * @FileName: LineApi.java
 * @author:   treylin
 * @date:     2014/11/11, 上午 10:46:25
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

package io.cslinmiso.line.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
import line.thrift.TalkService.Client;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;

/**
 * <pre> LineApi, TODO: add Class Javadoc here. </pre>
 * 
 * @author TreyLin
 */
public interface LineApi {


  /** The Constant LINE_DOMAIN. */
  public static final String LINE_DOMAIN = "http://gd2.line.naver.jp";

  /** The Constant LINE_HTTP_URL. */
  public static final String LINE_HTTP_URL = LINE_DOMAIN + "/api/v4/TalkService.do";
  
  /** The Constant LINE_HTTP_IN_URL. */
  public static final String LINE_HTTP_IN_URL = LINE_DOMAIN + "/P4";
  
  /** The Constant LINE_CERTIFICATE_URL. */
  public static final String LINE_CERTIFICATE_URL = LINE_DOMAIN + "/Q";
  
  /** The Constant LINE_SESSION_LINE_URL. */
  public static final String LINE_SESSION_LINE_URL = LINE_DOMAIN + "/authct/v1/keys/line";
  
  /** The Constant LINE_SESSION_NAVER_URL. */
  public static final String LINE_SESSION_NAVER_URL = LINE_DOMAIN + "/authct/v1/keys/naver";

  public static final String LINE_OBJECT_STORAGE_URL = "http://os.line.naver.jp/os/m/";
  
  public static final String LINE_STICKER_URL        = "http://dl.stickershop.line.naver.jp/products/0/0/";
  
  /** The Constant DATE_PATTERN. */
  public static final String DATE_PATTERN = "yyyyMMdd";

  /* (non-Javadoc)
   * @see io.cslinmiso.line.api.LineApi#ready()
   */
  public abstract Client ready() throws TTransportException;

  public abstract LoginResult login(String id, String password) throws Exception;

  /* (non-Javadoc)
   * @see io.cslinmiso.line.api.LineApi#loginWithAuthToken(java.lang.String)
   */
  public abstract void loginWithAuthToken(String authToken) throws Exception;

  /* (non-Javadoc)
   * @see io.cslinmiso.line.api.LineApi#loginWithQrCode()
   */
  public abstract AuthQrcode loginWithQrCode() throws Exception;

  /* (non-Javadoc)
   * @see io.cslinmiso.line.api.LineApi#loginWithVerifier()
   */
  public abstract String loginWithVerifier() throws Exception;

  /* (non-Javadoc)
   * @see io.cslinmiso.line.api.LineApi#getCertResult(java.lang.String)
   */
  public abstract Map getCertResult(String url) throws Exception;

  /* (non-Javadoc)
   * @see io.cslinmiso.line.api.LineApi#_findAndAddContactsByUserid(int, java.lang.String)
   */
  public abstract Map<String, Contact>  findAndAddContactsByUserid(int reqSeq, String userid)
      throws TalkException, TException;

  /* (non-Javadoc)
   * @see io.cslinmiso.line.api.LineApi#_findAndAddContactsByEmail(int, java.util.Set)
   */
  public abstract Map<String, Contact>  findAndAddContactsByEmail(int reqSeq, Set<String> emails)
      throws TalkException, TException;

  /* (non-Javadoc)
   * @see io.cslinmiso.line.api.LineApi#_findAndAddContactsByPhone(int, java.util.Set)
   */
  public abstract Map<String, Contact>  findAndAddContactsByPhone(int reqSeq, Set<String> phone)
      throws TalkException, TException;

  /* (non-Javadoc)
   * @see io.cslinmiso.line.api.LineApi#_getProfile()
   */
  public abstract Profile  getProfile() throws TalkException, TException;

  /* (non-Javadoc)
   * @see io.cslinmiso.line.api.LineApi#_getAllContactIds()
   */
  public abstract List<String>  getAllContactIds() throws TalkException, TException;

  /* (non-Javadoc)
   * @see io.cslinmiso.line.api.LineApi#_getBlockedContactIds()
   */
  public abstract List<String>  getBlockedContactIds() throws TalkException, TException;

  /* (non-Javadoc)
   * @see io.cslinmiso.line.api.LineApi#_getHiddenContactIds()
   */
  public abstract List<String>  getHiddenContactIds() throws TalkException, TException;

  /* (non-Javadoc)
   * @see io.cslinmiso.line.api.LineApi#_getContacts(java.util.List)
   */
  public abstract List<Contact>  getContacts(List<String> ids) throws TalkException, TException;

  /* (non-Javadoc)
   * @see io.cslinmiso.line.api.LineApi#_createRoom(int, java.util.List)
   */
  public abstract Room  createRoom(int reqSeq, List<String> ids) throws TalkException, TException;

  /* (non-Javadoc)
   * @see io.cslinmiso.line.api.LineApi#_getRoom(java.lang.String)
   */
  public abstract Room  getRoom(String roomId) throws TalkException, TException;

  /* (non-Javadoc)
   * @see io.cslinmiso.line.api.LineApi#_inviteIntoRoom(java.lang.String, java.util.List)
   */
  public abstract void  inviteIntoRoom(String roomId, List<String> contactIds)
      throws TalkException, TException;

  /* (non-Javadoc)
   * @see io.cslinmiso.line.api.LineApi#_leaveRoom(java.lang.String)
   */
  public abstract void  leaveRoom(String id) throws TalkException, TException;

  /* (non-Javadoc)
   * @see io.cslinmiso.line.api.LineApi#_createGroup(int, java.lang.String, java.util.List)
   */
  public abstract Group  createGroup(int seq, String name, List<String> ids) throws TalkException,
      TException;

  /* (non-Javadoc)
   * @see io.cslinmiso.line.api.LineApi#_getGroups(java.util.List)
   */
  public abstract List<Group>  getGroups(List<String> groupIds) throws TalkException, TException;

  /* (non-Javadoc)
   * @see io.cslinmiso.line.api.LineApi#_getGroupIdsJoined()
   */
  public abstract List<String>  getGroupIdsJoined() throws TalkException, TException;

  /* (non-Javadoc)
   * @see io.cslinmiso.line.api.LineApi#_getGroupIdsInvited()
   */
  public abstract List<String>  getGroupIdsInvited() throws TalkException, TException;

  /* (non-Javadoc)
   * @see io.cslinmiso.line.api.LineApi#_acceptGroupInvitation(int, java.lang.String)
   */
  public abstract void  acceptGroupInvitation(int seq, String groupId) throws TalkException,
      TException;

  /* (non-Javadoc)
   * @see io.cslinmiso.line.api.LineApi#_cancelGroupInvitation(int, java.lang.String, java.util.List)
   */
  public abstract void  cancelGroupInvitation(int seq, String groupId, List<String> contactIds)
      throws TalkException, TException;

  /* (non-Javadoc)
   * @see io.cslinmiso.line.api.LineApi#_inviteIntoGroup(int, java.lang.String, java.util.List)
   */
  public abstract void  inviteIntoGroup(int seq, String groupId, List<String> contactIds)
      throws TalkException, TException;

  /* (non-Javadoc)
   * @see io.cslinmiso.line.api.LineApi#_leaveGroup(java.lang.String)
   */
  public abstract void  leaveGroup(String id) throws TalkException, TException;

  /* (non-Javadoc)
   * @see io.cslinmiso.line.api.LineApi#_getRecentMessages(java.lang.String, int)
   */
  public abstract List<Message>  getRecentMessages(String id, int count) throws TalkException,
      TException;

  /* (non-Javadoc)
   * @see io.cslinmiso.line.api.LineApi#_sendMessage(int, thrift.Message)
   */
  public abstract Message  sendMessage(int seq, Message message) throws TalkException, TException;

  /* (non-Javadoc)
   * @see io.cslinmiso.line.api.LineApi#_getLastOpRevision()
   */
  public abstract long  getLastOpRevision() throws TalkException, TException;

  /* (non-Javadoc)
   * @see io.cslinmiso.line.api.LineApi#_fetchOperations(long, int)
   */
  public abstract List<Operation>  fetchOperations(long revision, int count) throws TalkException,
      TException;

  /* (non-Javadoc)
   * @see io.cslinmiso.line.api.LineApi#_getMessageBoxCompactWrapUp(java.lang.String)
   */
  public abstract TMessageBoxWrapUp  getMessageBoxCompactWrapUp(String id);

  /* (non-Javadoc)
   * @see io.cslinmiso.line.api.LineApi#_getMessageBoxCompactWrapUpList(int, int)
   */
  public abstract TMessageBoxWrapUpResponse  getMessageBoxCompactWrapUpList(int start, int count)
      throws Exception;

}
