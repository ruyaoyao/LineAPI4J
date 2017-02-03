/**
 * 
 * @Package: io.cslinmiso.line.model
 * @FileName: LineGroup.java
 * @author: treylin
 * @date: 2014/11/24, 下午 03:14:20
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

import line.thrift.Contact;
import line.thrift.Group;

import java.util.ArrayList;
import java.util.List;

public class LineGroup extends LineBase {
  /**
   * LineGroup wrapper
   * 
   * Attributes: creator contact of group creator members list of contact of group members invitee
   * list of contact of group invitee
   * 
   * >>> group = LineGroup(client, client.groups[0])
   */

  Group group;
  String name;
  boolean isJoined = true;

  LineContact creator;
  List<LineContact> members = new ArrayList<LineContact>();
  List<LineContact> invitee = new ArrayList<LineContact>();

  public LineGroup() {}

  public LineGroup(LineClient client, Group group) {
    /**
     * LineGroup init
     * 
     * :param client: LineClient instance :param group: Group instace :param is_joined: is a user
     * joined or invited to a group
     */
    this.setClient(client);
    this.setGroup(group);
    this.setName(group.getName());
    this.setId(group.getId());

    this.setJoined(isJoined);

    try {
      setCreator(new LineContact(client, group.getCreator()));
    } catch (Exception e) {
      setCreator(null);
    }

    this.members.clear();
    for (Contact member : group.getMembers()) {
      this.members.add(new LineContact(client, member));
    }

    this.invitee.clear();
    if(group.getInvitee()!=null){
      if (group.getInvitee().size() >= 1) {
        for (Contact member : group.getInvitee()) {
          this.invitee.add(new LineContact(client, member));
        }
      }
    }
  }

  public boolean acceptGroupInvitation() throws Exception {
    if (isJoined() == false) {
      this.client.acceptGroupInvitation(this);
      return true;
    } else {
      throw new Exception("You are already in group.");
    }
  }

  public boolean leave() throws Exception {
    /** Leave group **/
    if (isJoined()) {
      try {
        return this.client.leaveGroup(this);
      } catch (Exception e) {
        return false;
      }
    } else {
      throw new Exception("You are not joined to group");
    }
  }

  public List<String> getMemberIds() {
    /** Get member ids of group */

    List<String> contactIds = new ArrayList<String>();

    for (LineContact member : members) {
      contactIds.add(member.getId());
    }

    return contactIds;
  }

  @Override
  public String toString() {
    /** Name of Group and number of group members **/
    if (isJoined()) {
      return "<LineGroup " + this.getName() + " #" + this.members.size() + ">";
    } else {
      return "<LineGroup " + this.getName() + " #" + this.members.size() + " (invited)>";
    }
  }
  
  public boolean containsMemberId(String id) {
    for (LineContact member : members) {
      if (id.equals(member.getId())) {
          return true;
      }
    }
    return false;
  } 
  
  public Group getGroup() {
    return group;
  }

  public void setGroup(Group group) {
    this.group = group;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isJoined() {
    return isJoined;
  }

  public void setJoined(boolean isJoined) {
    this.isJoined = isJoined;
  }

  public LineContact getCreator() {
    return creator;
  }

  public void setCreator(LineContact creator) {
    this.creator = creator;
  }

  public List<LineContact> getMembers() {
    return members;
  }

  public void setMembers(List<LineContact> members) {
    this.members = members;
  }

  public List<LineContact> getInvitee() {
    return invitee;
  }

  public void setInvitee(List<LineContact> invitee) {
    this.invitee = invitee;
  }
}
