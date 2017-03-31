/**
 * 
 * @Package: io.cslinmiso.line.model
 * @FileName: LineRoom.java
 * @author: treylin
 * @date: 2014/11/24, 下午 03:14:20
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

package io.cslinmiso.line.model;

import line.thrift.Contact;
import line.thrift.Room;
import line.thrift.TalkException;
import org.apache.thrift.TException;

import java.util.ArrayList;
import java.util.List;

public class LineRoom extends LineBase {

  /**
   * Chat room wrapper
   * 
   * Attributes: contacts Contact list of chat room
   **/

  Room room;
  List<LineContact> contacts;

  public LineRoom() {}
  
  public LineRoom(LineClient client, Room room) {
    /**
     * LineContact init
     * 
     * :param client: LineClient instance :param room: Room instace
     **/
    this.setClient(client);
    this.setRoom(room);

    this.setId(room.getMid());

    this.contacts = new ArrayList<LineContact>();

    for (Contact contact : room.getContacts()) {
      this.contacts.add(new LineContact(client, contact));
    }

  }

  public boolean leave() throws TalkException, TException, Exception {
    /** Leave room */
    return this.client.leaveRoom(this);
  }

  public void invite(LineContact contact) throws TalkException, TException, Exception {
    /**
     * Invite into group :param contact: LineContact instance to invite
     */
    List<LineContact> temp = new ArrayList<LineContact>();
    temp.add(contact);
    this.client.inviteIntoRoom(this, temp);
  }


  public List<String> getContactIds() {
    /** Get contact ids of room */

    List<String> contactIds = new ArrayList<String>();

    for (LineContact contact : contacts) {
      contactIds.add(contact.getId());
    }

    return contactIds;
  }

  public Room getRoom() {
    return room;
  }

  public void setRoom(Room room) {
    this.room = room;
  }

  public List<LineContact> getContacts() {
    return contacts;
  }

  public void setContacts(List<LineContact> contacts) {
    this.contacts = contacts;
  }

  @Override
  public String toString() {
    // return '<LineContact %s (%s)>' % (self.name, self.id)
    return "<LineRoom " + this.contacts + ">";

  }

  public boolean containsId(String id) {
    for (LineContact contact : contacts) {
      if (id.equals(contact.getId())) {
          return true;
      }
    }
    return false;
  } 
  
}
