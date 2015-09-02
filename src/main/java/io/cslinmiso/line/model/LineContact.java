/**
 * 
 * @Package:  io.cslinmiso.line.model
 * @FileName: LineContact.java
 * @author:   trey
 * @date:     2015/9/2, 下午 02:39:39
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

import java.util.ArrayList;
import java.util.List;

import line.thrift.Contact;

/**
 * The Class LineContact.
 */
public class LineContact extends LineBase {
  
  /**
   * LineContact wrapper 
   * Attributes: 
   * name display name of contact 
   * statusMessage status message of contact.
   */

  Contact _contact;
  
  /** The name. */
  String name;
  
  /** The status message. */
  String statusMessage;
  
  /** The rooms. */
  List<LineRoom> rooms;
  
  /** The groups. */
  List<LineGroup> groups;

  /**
   * Instantiates a new line contact.
   */
  public LineContact() {}

  /**
   * Instantiates a new line contact.
   * 
   * @param client the client
   * @param contact the contact
   */
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

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    // return '<LineContact %s (%s)>' % (self.name, self.id)
    return "<LineContact " + this.name + ">";

  }

  /**
   * Gets the contact.
   * 
   * @return the contact
   */
  public Contact getContact() {
    return _contact;
  }

  /**
   * Sets the contact.
   * 
   * @param _contact the new contact
   */
  public void setContact(Contact _contact) {
    this._contact = _contact;
  }

  /**
   * Gets the name.
   * 
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   * 
   * @param name the new name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the status message.
   * 
   * @return the status message
   */
  public String getStatusMessage() {
    return statusMessage;
  }

  /**
   * Sets the status message.
   * 
   * @param statusMessage the new status message
   */
  public void setStatusMessage(String statusMessage) {
    this.statusMessage = statusMessage;
  }

  /**
   * Gets the rooms.
   * 
   * @return the rooms
   */
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

  /**
   * Sets the rooms.
   * 
   * @param rooms the new rooms
   */
  public void setRooms(List<LineRoom> rooms) {
    this.rooms = rooms;
  }

  /**
   * Gets the groups.
   * 
   * @return the groups
   */
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

  /**
   * Sets the groups.
   * 
   * @param groups the new groups
   */
  public void setGroups(List<LineGroup> groups) {
    this.groups = groups;
  }

  public long getCreatedTime() {
    return this._contact.createdTime;
  }

  public line.thrift.ContactType getType() {
    return this._contact.type;
  }

  public line.thrift.ContactStatus getStatus() {
    return this._contact.status;
  }

  public line.thrift.ContactRelation getRelation() {
    return this._contact.relation;
  }

  public java.lang.String getDisplayName() {
    return this._contact.displayName;
  }

  public java.lang.String getPhoneticName() {
    return this._contact.phoneticName;
  }

  public java.lang.String getPictureStatus() {
    return this._contact.pictureStatus;
  }

  public java.lang.String getThumbnailUrl() {
    return this._contact.thumbnailUrl;
  }

  public java.lang.String getDisplayNameOverridden() {
    return this._contact.displayNameOverridden;
  }

  public long getFavoriteTime() {
    return this._contact.favoriteTime;
  }

  public boolean isCapableVoiceCall() {
    return this._contact.capableVoiceCall;
  }

  public boolean isCapableVideoCall() {
    return this._contact.capableVideoCall;
  }

  public boolean isCapableMyhome() {
    return this._contact.capableMyhome;
  }

  public boolean isCapableBuddy() {
    return this._contact.capableBuddy;
  }

  public int getAttributes() {
    return this._contact.attributes;
  }

  public long getSettings() {
    return this._contact.settings;
  }

  public java.lang.String getPicturePath() {
    return this._contact.picturePath;
  }

}
