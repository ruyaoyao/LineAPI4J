/**
 * 
 * @Package: io.cslinmiso.line.model
 * @FileName: LineMessage.java
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

import line.thrift.Message;



/**
 * The Class LineMessage wrapper.
 */
public class LineMessage extends Message{

  private static final long serialVersionUID = 8788970788991926078L;
  
  public LineBase sender;
  public LineBase receiver;

  /**
   * # toType # 0: User # 1: Room # 2: Group
   **/
//  public MIDType toType;
//  public long createdTime;

  public LineMessage() {}

  public LineMessage(LineClient client, Message message) {
    this.setId(message.getId());
    this.setText(message.getText());

    this.setHasContent(message.isHasContent());
    this.setContentType(message.getContentType());
    this.setContentPreview(message.getContentPreview());
    this.setContentMetadata(message.getContentMetadata());

    this.setSender(client.getContactOrRoomOrGroupById(message.getFrom()));
    this.setReceiver( client.getContactOrRoomOrGroupById(message.getTo()));

    /**
     * # toType # 0: User # 1: Room # 2: Group
     **/
    this.setToType( message.getToType());
    this.setCreatedTime(message.getCreatedTime());
  }

  public LineBase getSender() {
    return sender;
  }

  public void setSender(LineBase sender) {
    this.sender = sender;
  }

  public LineBase getReceiver() {
    return receiver;
  }

  public void setReceiver(LineBase receiver) {
    this.receiver = receiver;
  }
  
  
  
}
