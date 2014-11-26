/**
 * 
 * @Package: io.cslinmiso.line.model
 * @FileName: LineContact.java
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

import java.util.ArrayList;
import java.util.List;

import line.thrift.Contact;

public class LineContact extends LineBase {
  /**
   * LineContact wrapper
   * 
   * Attributes: name display name of contact statusMessage status message of contact
   */

  Contact _contact;
  String name;
  String statusMessage;
  List<LineRoom> rooms;
  List<LineGroup> groups;

  public LineContact() {}

  public LineContact(LineClient client, Contact contact) {
    /**
     * LineContact init
     * 
     * :param client: LineClient instance :param contact: Conatct instace
     */

    this.setClient(client);
    this.setContact(contact);

    this.setId(contact.getMid());
    this.setName(contact.getDisplayName());
    this.setStatusMessage(contact.getStatusMessage());

  }

  @Override
  public String toString() {
    // return '<LineContact %s (%s)>' % (self.name, self.id)
    return "<LineContact " + this.name + ">";

  }

  public Contact getContact() {
    return _contact;
  }

  public void setContact(Contact _contact) {
    this._contact = _contact;
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getStatusMessage() {
    return statusMessage;
  }

  public void setStatusMessage(String statusMessage) {
    this.statusMessage = statusMessage;
  }

  public List<LineRoom> getRooms() {
    /** Rooms that contact participates **/
    if (this.rooms == null) {
      rooms = new ArrayList<LineRoom>();
    }

    for (LineRoom room : this._client.rooms) {
      if (room.containsId(this.getId())) {
        rooms.add(room);
      }
    }
    return rooms;
  }

  public void setRooms(List<LineRoom> rooms) {
    this.rooms = rooms;
  }

  public List<LineGroup> getGroups() {
    /** Groups that contact participates **/
    if (this.groups == null) {
      groups = new ArrayList<LineGroup>();
    }

    for (LineGroup group : this._client.groups) {
      if (group.containsMemberId(this.getId())) {
        this.groups.add(group);
      }
    }
    return groups;
  }

  public void setGroups(List<LineGroup> groups) {
    this.groups = groups;
  }

}
