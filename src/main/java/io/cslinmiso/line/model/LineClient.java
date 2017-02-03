/**
 * 
 * @Package: io.cslinmiso.line.model
 * @FileName: LineClient.java
 * @author: trey
 * @date: 2015/9/2, 下午 02:43:57
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
package io.cslinmiso.line.model;

import io.cslinmiso.line.api.LineApi;
import io.cslinmiso.line.api.impl.LineApiImpl;
import line.thrift.Contact;
import line.thrift.ContentType;
import line.thrift.ErrorCode;
import line.thrift.Group;
import line.thrift.MIDType;
import line.thrift.Message;
import line.thrift.OpType;
import line.thrift.Operation;
import line.thrift.Profile;
import line.thrift.TMessageBox;
import line.thrift.TMessageBoxWrapUp;
import line.thrift.TMessageBoxWrapUpResponse;
import line.thrift.TalkException;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Trey Lin
 */
public class LineClient implements Closeable {

  private final LineApi api;
  /** The revision. */
  public long revision;

  Profile profile;
  List<LineContact> contacts;
  List<LineRoom> rooms;
  List<LineGroup> groups;

  public LineClient() throws Exception {
    this(new LineApiImpl());
  }

  public LineClient(LineApi api) throws Exception {
    this.api = api;
  }

  public void login(@Nonnull String id, @Nonnull String password) throws Exception {
    login(id, password, null, null);
  }

  public void login(
          @Nonnull String id,
          @Nonnull String password,
          @Nullable String certificate,
          @Nullable LoginCallback loginCallback) throws Exception {
    this.api.login(id, password, certificate, loginCallback);
    init();
  }

  public void loginWithAuthToken(@Nonnull String authToken)
          throws Exception {
    this.api.loginWithAuthToken(authToken);
    init();
  }

  private void init() throws Exception {
    // 認證通過後登入
    // String auth = api.loginWithVerifier();
    // Set up authToken
    // initialize
    this.setRevision(this.api.getLastOpRevision());
    this.getProfile();
    try {
      this.refreshGroups();
    } catch (Exception e) {
      // pass
    }
    try {
      this.refreshContacts();
    } catch (Exception e) {
      // pass
    }
    try {
      this.refreshActiveRooms();
    } catch (Exception e) {
      // pass
    }
  }

  /**
   * 用LINE ID搜尋並加入好友.
   * 
   * @param userid the userid
   * @return LineContact
   * @throws Exception the exception
   */
  public LineContact findContactByUserid(String userid) throws Exception {

    if (checkAuth()) {
      Contact thatContact;
      try {
        thatContact = this.api.findContactByUserid(userid);
        LineContact lineContact = new LineContact(this, thatContact);
        return lineContact;
      } catch (TalkException te) {
        new Exception(te.getReason());
      }
    }
    return null;
  }

  private LineContact verifyContact(Map<String, Contact> contactMap) throws Exception {
    if (contactMap != null && contactMap.size() > 0) {
      Contact thatContact = contactMap.get(0);

      for (LineContact tmpContact : this.contacts) {
        Contact contact = tmpContact.getContact();
        if (contact.equals(thatContact)) {
          throw new Exception(String.format("%s already exists.", thatContact.getDisplayName()));
        }
      }
      LineContact lineContact = new LineContact(this, thatContact);
      this.contacts.add(lineContact);

      return lineContact;
    }
    return null;
  }

  /**
   * 用LINE ID搜尋並加入好友.
   * 
   * @param userid the userid
   * @return the map
   * @throws Exception the exception
   */
  public LineContact findAndAddContactsByUserid(String userid) throws Exception {

    if (checkAuth()) {
      try {
        Map<String, Contact> contactMap = this.api.findAndAddContactsByUserid(0, userid);
        return verifyContact(contactMap);
      } catch (TalkException te) {
        new Exception(te.getReason());
      }
    }
    return null;
  }

  /**
   * 用email搜尋並加入好友 (沒測試成功)
   * 
   * @return
   * @throws Exception
   **/
  public LineContact findAndAddContactsByEmail(Set<String> emails) throws Exception {

    if (checkAuth()) {
      try {
        Map<String, Contact> contactMap = this.api.findAndAddContactsByEmail(0, emails);
        return verifyContact(contactMap);
      } catch (TalkException te) {
        new Exception(te.getReason());
      }
    }
    return null;
  }

  /**
   * 用電話搜尋並加入好友 (沒測試成功)
   * 
   * @return
   * @return
   * @throws Exception
   **/
  public LineContact findAndAddContactsByPhone(Set<String> phone) throws Exception {
    if (checkAuth()) {
      try {
        Map<String, Contact> contactMap = this.api.findAndAddContactsByPhone(0, phone);
        return verifyContact(contactMap);
      } catch (TalkException te) {
        new Exception(te.getReason());
      }
    }
    return null;
  }

  public Profile getProfile() throws Exception {
    /**
     * Get profile information
     * 
     * returns Profile object; - picturePath - displayName - phone (base64 encoded?) -
     * allowSearchByUserid - pictureStatus - userid - mid # used for unique id for account -
     * phoneticName - regionCode - allowSearchByEmail - email - statusMessage
     **/

    /** Get `profile` of LINE account **/
    if (checkAuth()) {
      if (this.profile == null) {
        this.profile = this.api.getProfile();
        return profile;
      } else {
        return this.profile;
      }
    } else {
      return null;
    }
  }

  public LineContact getContactByName(String name) {
    for (LineContact contact : contacts) {
      if (contact.getName().equals(name)) {
        return contact;
      }
    }
    return null;
  }

  public LineContact getContactById(String id){
    for (LineContact contact : contacts) {
      if (contact.getId().equals(id)) {
        return contact;
      }
    }
    return null;
  }

  public LineBase getContactOrRoomOrGroupById(String id) {
    // Get a `contact` or `room` or `group` by its id
    List<LineBase> list = new ArrayList<LineBase>();
    list.add(getContactById(id));
    list.add(getGroupById(id));
    list.add(getRoomById(id));
    list.removeAll(Collections.singleton(null)); // 移除null
    return list.size() >= 1 ? list.get(0) : null;
  }

  public void refreshGroups() throws Exception {
    // Refresh groups of LineClient
    // Refresh active chat rooms
    if (checkAuth()) {

      this.groups = new ArrayList<LineGroup>();
      List<String> groupIdsJoined = this.api.getGroupIdsJoined();
      List<String> groupIdsInvited = this.api.getGroupIdsInvited();

      addGroupsWithIds(groupIdsJoined);
      addGroupsWithIds(groupIdsInvited);

    }
  }

  public void addGroupsWithIds(List<String> groupIds) throws TalkException, TException, Exception {
    /** Refresh groups of LineClient */
    if (checkAuth()) {
      List<Group> newGroups = this.api.getGroups(groupIds);

      for (Group group : newGroups) {
        this.groups.add(new LineGroup(this, group));
      }
      // self.groups.sort()
    }
  }

  public void refreshContacts() throws TalkException, TException, Exception {
    /** Refresh contacts of LineClient **/
    if (checkAuth()) {
      List<String> contactIds = this.api.getAllContactIds();
      List<Contact> contactsLocal = this.api.getContacts(contactIds);

      this.contacts = new ArrayList<LineContact>();

      for (Contact contact : contactsLocal) {
        this.contacts.add(new LineContact(this, contact));
      }
    }
  }

  public List<LineContact> getHiddenContacts() throws Exception {
    // Refresh groups of LineClient
    if (checkAuth()) {
      List<String> contactIds = this.api.getBlockedContactIds();
      List<Contact> contactsLocal = this.api.getContacts(contactIds);

      List<LineContact> c = new ArrayList<LineContact>();

      for (Contact contact : contactsLocal) {
        c.add(new LineContact(this, contact));
      }
      return c;
    }
    return null;
  }

  public void refreshActiveRooms() throws TalkException, TException, Exception {
    // Refresh active chat rooms
    if (checkAuth()) {
      int start = 1;
      int count = 50;

      this.rooms = new ArrayList<LineRoom>();

      while (true) {
        TMessageBoxWrapUpResponse channel = this.api.getMessageBoxCompactWrapUpList(start, count);
        for (TMessageBoxWrapUp box : channel.messageBoxWrapUpList) {
          if (box.messageBox.midType == MIDType.ROOM) {
            LineRoom room = new LineRoom(this, this.api.getRoom(box.messageBox.id));
            this.rooms.add(room);
          }
        }
        if (channel.messageBoxWrapUpList.size() == count) {
          start += count;
        } else {
          break;
        }
      }
    }
  }

  public LineGroup createGroupWithIds(String name, List<String> ids) throws TalkException,
      TException, Exception {
    /**
     * Create a group with contact ids
     * 
     * :param name: name of group :param ids: list of contact ids
     **/
    if (checkAuth()) {

      LineGroup group = new LineGroup(this, this.api.createGroup(0, name, ids));
      this.groups.add(group);
      return group;
    }
    return null;
  }

  public LineGroup createGroupWithContacts(String name, List<LineContact> contacts)
      throws TalkException, TException, Exception {
    /*
     * Create a group with contacts
     * 
     * :param name: name of group :param contacts: list of contacts
     */
    if (checkAuth()) {

      List<String> contactIds = new ArrayList<String>();

      for (LineContact contact : contacts) {
        contactIds.add(contact.getId());
      }

      LineGroup group = new LineGroup(this, this.api.createGroup(0, name, contactIds));
      this.groups.add(group);

      return group;
    }
    return null;
  }


  public LineGroup getGroupByName(String name) {
    /**
     * Get a group by name
     * 
     * :param name: name of a group
     **/
    for (LineGroup group : this.groups) {
      if (name.equals(group.getName())) {
        return group;
      }
    }

    return null;

  }

  public LineGroup getGroupById(String id) {
    /*
     * Get a group by id
     * 
     * :param id: id of a group
     */

    for (LineGroup group : this.groups) {
      if (id.equals(group.getId())) {
        return group;
      }
    }

    return null;

  }

  public void inviteIntoGroup(String groupId, List<String> contactIds) throws Exception {
    this.api.inviteIntoGroup(0, groupId, contactIds);
  }
  
  public void inviteIntoGroup(LineGroup group, List<LineContact> contacts) throws Exception {
    /*
     * Invite contacts into group
     * 
     * :param group: LineGroup instance :param contacts: LineContact instances to invite
     */
    if (checkAuth()) {
      List<String> contactIds = new ArrayList<String>();
      for (LineContact contact : contacts) {
        contactIds.add(contact.getId());
      }
      inviteIntoGroup(group.getId(), contactIds);
    }
  }

  public void kickoutFromGroup(LineGroup group, List<String> contactIds) throws Exception {
    /** Kick a group members **/
    // seq = 0;
    if (checkAuth()) {
      this.api.kickoutFromGroup(0, group.getId(), contactIds);
    }
  }

  public boolean acceptGroupInvitation(LineGroup group) throws Exception {
    /**
     * Accept a group invitation
     * 
     * :param group: LineGroup instance
     **/
    if (checkAuth()) {

      this.api.acceptGroupInvitation(0, group.getId());
      return true;
    }
    return false;
  }

  public boolean leaveGroup(LineGroup group) throws Exception {
    /*
     * Leave a group
     * 
     * :param group: LineGroup instance to leave
     */
    if (checkAuth()) {
      this.api.leaveGroup(group.getId());
      return this.groups.remove(group);
    }
    return false;

  }

  public LineRoom createRoomWithIds(List<String> ids) throws TalkException, TException, Exception {
    /** Create a chat room with contact ids **/
    if (checkAuth()) {

      LineRoom room = new LineRoom(this, this.api.createRoom(ids.size(), ids));
      this.rooms.add(room);

      return room;
    }
    return null;

  }

  public LineRoom createRoomWithContacts(List<LineContact> contacts) throws TalkException,
      TException, Exception {
    /** Create a chat room with contacts **/
    if (checkAuth()) {
      List<String> contactIds = new ArrayList<String>();

      for (LineContact contact : contacts) {
        contactIds.add(contact.getId());
      }

      return createRoomWithIds(contactIds);
    }
    return null;

  }

  public LineRoom getRoomById(String id) {
    /**
     * Get a room by id
     * 
     * :param id: id of a room
     **/

    for (LineRoom room : this.rooms) {
      if (id.equals(room.getId())) {
        return room;
      }
    }
    return null;

  }

  public void inviteIntoRoom(LineRoom room, List<LineContact> contacts) throws TalkException,
      TException, Exception {
    /**
     * Invite contacts into room
     * 
     * :param room: LineRoom instance :param contacts: LineContact instances to invite
     **/
    if (checkAuth()) {
      List<String> contactIds = new ArrayList<String>();

      for (LineContact contact : contacts) {
        contactIds.add(contact.getId());
      }

      this.api.inviteIntoRoom(room.getId(), contactIds);
    }
  }

  public boolean leaveRoom(LineRoom room) throws TalkException, TException, Exception {
    /**
     * Leave a room
     * 
     * :param room: LineRoom instance to leave
     **/
    if (checkAuth()) {
      this.api.leaveRoom(room.getId());
      return this.rooms.remove(room);
    }
    return false;

  }

  /**
   * Send a message
   * 
   * :param message: LineMessage instance to send
   */
  public Message sendMessage(int seq, LineMessage message) throws TalkException, TException,
      Exception {
    if (checkAuth()) {
      // seq = 0;
      try {
        return this.api.sendMessage(seq, message);
      } catch (TalkException e) {
        this.api.updateAuthToken();
        try {
          return this.api.sendMessage(seq, message);
        } catch (Exception ex) {
          throw ex;
        }
      }
    }
    return null;

  }

  public TMessageBox getMessageBox(String id) throws Exception {
    /**
     * Get MessageBox by id
     * 
     * :param id: `contact` id or `group` id or `room` id
     */
    if (checkAuth()) {
      TMessageBoxWrapUp messageBoxWrapUp = this.api.getMessageBoxCompactWrapUp(id);

      return messageBoxWrapUp.getMessageBox();
    }
    return null;

  }

  public List<LineMessage> getRecentMessages(TMessageBox messageBox, int count)
      throws TalkException, TException, Exception {
    /**
     * Get recent message from MessageBox
     * 
     * :param messageBox: MessageBox object
     */
    if (checkAuth()) {
      String id = messageBox.getId();
      List<Message> messages = this.api.getRecentMessages(id, count);

      return this.getLineMessageFromMessage(messages);
    }
    return null;
  }

  public void longPoll() throws TalkException, TException, Exception {
    // default count as 50
    longPoll(50);
  }

  public void longPoll(int count) throws TalkException, TException, Exception {
    /**
     * Receive a list of operations that have to be processed by original Line client.
     * 
     * :param count: number of operations to get from :returns: a generator which returns operations
     * 
     * >>> for op in client.longPoll(){ sender = op[0] receiver = op[1] message = op[2] print
     * "%s->%s : %s" % (sender, receiver, message)
     */
    // count = 50;
    if (checkAuth()) {
      /* Check is there any operations from LINE server */

      List<Operation> operations = new ArrayList<Operation>();

      try {
        operations = this.api.fetchOperations(this.getRevision(), count);
      } catch (TalkException e) {
        if (ErrorCode.INVALID_MID == e.getCode()) {
          throw new Exception("user logged onto another machine");
        } else {
          return;
        }
      } catch (TTransportException e) {
        if (e.getMessage().indexOf("204") != -1) {
          return;
        } else {
          return;
        }
      } catch (Exception e) {
        System.out.println(e);
      }

      List<String> excepCodes = Arrays.asList(new String[] {"60", "61"});

      for (Operation operation : operations) {
        OpType opType = operation.getType();
        Message msg = operation.getMessage();
        if (opType == OpType.END_OF_OPERATION) {

        } else if (opType == OpType.SEND_MESSAGE) {

        } else if (opType == OpType.RECEIVE_MESSAGE) {
          LineMessage message = new LineMessage(this, msg);
          if (msg.getContentType() == ContentType.VIDEO
              || msg.getContentType() == ContentType.IMAGE) {
            continue;
          }

          String id;
          String rawMid = getProfile().getMid();
          String rawSender = operation.getMessage().getFrom();
          String rawReceiver = operation.getMessage().getTo();

          // id = 實際發送者, whom sent the message
          id = rawReceiver;
          if (rawReceiver.equals(rawMid)) {
            id = rawSender;
          }

          LineBase sender = this.getContactOrRoomOrGroupById(rawSender);
          LineBase receiver = this.getContactOrRoomOrGroupById(rawReceiver);

          // If sender is not found, check member list of group chat sent to
          if (sender == null && (receiver instanceof LineGroup || receiver instanceof LineRoom)) {
            List<LineContact> contactsLocal =
                receiver instanceof LineGroup ? ((LineRoom) receiver).getContacts()
                // If sender is not found, check member list of room chat sent to
                    : ((LineGroup) receiver).getMembers();
            for (LineContact contact : contactsLocal) {
              if (contact.getId().equals(rawSender)) {
                sender = contact;
                break;
              }
            }
          }

          if (sender == null || receiver == null) {
            this.refreshGroups();
            this.refreshContacts();
            this.refreshActiveRooms();

            sender = this.getContactOrRoomOrGroupById(rawSender);
            receiver = this.getContactOrRoomOrGroupById(rawReceiver);
          }

          if (sender == null || receiver == null) {
            List<Contact> contactsLocal =
                this.getApi().getContacts(Arrays.asList(new String[] {rawSender, rawReceiver}));
            if (contactsLocal != null && contactsLocal.size() == 2) {
              sender = new LineContact(this, contactsLocal.get(0));
              receiver = new LineContact(this, contactsLocal.get(1));
            }
            System.out.printf("[*] sender: %s  receiver: %s\n", sender, receiver);
            // yield (sender, receiver, message);
          } else {
            System.out.printf("Sender:%s \t Receiver:%s\n [*] %s\n", sender, receiver,
                OpType.findByValue(operation.getType().getValue()));
          }

        } else if (excepCodes.contains(opType)) {
          // pass
        } else {
          System.out.printf("[*] %s\n", OpType.findByValue(operation.getType().getValue()));
        }
        this.revision = Math.max(operation.getRevision(), this.revision);
      }

    }

  }

  public void createContactOrRoomOrGroupByMessage(Message message) {
    if (message.getToType() == MIDType.USER) {

    } else if (message.getToType() == MIDType.ROOM) {

    } else if (message.getToType() == MIDType.GROUP) {

    }
  }

  public List<LineMessage> getLineMessageFromMessage(List<Message> messages) {
    /*
     * Change Message objects to LineMessage objects
     * 
     * :param messges: list of Message object
     */
    List<LineMessage> lineMessages = new ArrayList<LineMessage>();

    for (Message message : messages) {
      lineMessages.add(new LineMessage(this, message));
    }
    return lineMessages;
  }

  public boolean checkAuth() throws Exception {
    /** Check if client is logged in or not **/
    if (getAuthToken() != null) {
      return true;
    } else {
      throw new Exception("you need to login");
    }
  }

  public LineApi getApi() {
    return api;
  }

  public String getAuthToken() {
    return api.getAuthToken();
  }

  public long getRevision() {
    return revision;
  }

  public void setRevision(long revision) {
    this.revision = revision;
  }

  public List<LineContact> getContacts() {
    return contacts;
  }

  public void setContacts(List<LineContact> contacts) {
    this.contacts = contacts;
  }

  public List<LineRoom> getRooms() {
    return rooms;
  }

  public void setRooms(List<LineRoom> rooms) {
    this.rooms = rooms;
  }

  public List<LineGroup> getGroups() {
    return groups;
  }

  public void setGroups(List<LineGroup> groups) {
    this.groups = groups;
  }

  public void setProfile(Profile profile) {
    this.profile = profile;
  }

  public String getCertificate() {
    return api.getCertificate();
  }

  @Override
  public void close() throws IOException {
    api.close();
  }
}
