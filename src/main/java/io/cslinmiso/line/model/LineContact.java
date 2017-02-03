/**
 * 
 * @Package:  io.cslinmiso.line.model
 * @FileName: LineContact.java
 * @author:   trey
 * @date:     2016/03/28, 下午 02:39:39
 * 
 * <pre>
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Trey Lin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons profileImageto whom the Software is
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
import line.thrift.Contact;

import java.util.ArrayList;
import java.util.List;

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

  Contact contact;
  
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
    return contact;
  }

  /**
   * Sets the contact.
   * 
   * @param contact the new contact
   */
  public void setContact(Contact contact) {
    this.contact = contact;
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

    for (LineRoom room : this.client.rooms) {
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

    for (LineGroup group : this.client.groups) {
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
    return this.contact.createdTime;
  }

  public line.thrift.ContactType getType() {
    return this.contact.type;
  }

  public line.thrift.ContactStatus getStatus() {
    return this.contact.status;
  }

  public line.thrift.ContactRelation getRelation() {
    return this.contact.relation;
  }

  public java.lang.String getDisplayName() {
    return this.contact.displayName;
  }

  public java.lang.String getPhoneticName() {
    return this.contact.phoneticName;
  }

  public java.lang.String getPictureStatus() {
    return this.contact.pictureStatus;
  }

  public java.lang.String getThumbnailUrl() {
    return this.contact.thumbnailUrl;
  }

  public java.lang.String getDisplayNameOverridden() {
    return this.contact.displayNameOverridden;
  }

  public long getFavoriteTime() {
    return this.contact.favoriteTime;
  }

  public boolean isCapableVoiceCall() {
    return this.contact.capableVoiceCall;
  }

  public boolean isCapableVideoCall() {
    return this.contact.capableVideoCall;
  }

  public boolean isCapableMyhome() {
    return this.contact.capableMyhome;
  }

  public boolean isCapableBuddy() {
    return this.contact.capableBuddy;
  }

  public int getAttributes() {
    return this.contact.attributes;
  }

  public long getSettings() {
    return this.contact.settings;
  }

  public String getPicturePath() {
    return LineApi.LINE_PROFILE_URL + this.contact.picturePath;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((contact== null) ? 0 : contact.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    LineContact other = (LineContact) obj;
    if (contact == null) {
      if (other.contact != null) return false;
    } else if (!contact.getMid().equals(other.contact.getMid())) return false;
    return true;
  }

  
  
}
