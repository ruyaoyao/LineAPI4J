/**
 * 
 * @Package: io.cslinmiso.line.model
 * @FileName: LineBase.java
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

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import line.thrift.ContentType;
import line.thrift.TMessageBox;
import line.thrift.TalkException;

import org.apache.commons.io.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;


/**
 * The Class LineBase.
 */
public class LineBase {

  /** The id. */
  public String id;

  /** The _client. */
  public LineClient _client;

  // _messageBox = null;


  /**
   * Send message.
   * 
   * @param text the text
   * @return true, if successful
   * @throws TalkException the talk exception
   * @throws TException the t exception
   * @throws Exception the exception
   */
  public boolean sendMessage(String text) throws TalkException, TException, Exception {
    /**
     * Send a message
     * 
     * :param text: text message to send
     **/

    LineMessage message = new LineMessage();
    message.setTo(getId());
    message.setText(text);
    _client.sendMessage(0, message);

    return true;

  }

  // public void sendSticker(
  // stickerId = "13",
  // stickerPackageId = "1",
  // stickerVersion = "100",
  // stickerText="[null]")

  /**
   * Send sticker.
   * 
   * @param stickerId the sticker id
   * @param stickerPackageId the sticker package id
   * @param stickerVersion the sticker version
   * @param stickerText the sticker text
   * @return true, if successful
   * @throws TalkException the talk exception
   * @throws TException the t exception
   * @throws Exception the exception
   */
  public boolean sendSticker(String stickerId, String stickerPackageId, String stickerVersion,
      String stickerText) throws TalkException, TException, Exception {
    /**
     * Send a sticker
     * 
     * :param stickerId: id of sticker :param stickerPackageId: package id of sticker :param
     * stickerVersion: version of sticker :param stickerText: text of sticker (}public
     * voidault='[null]')
     **/

    LineMessage message = new LineMessage();
    message.setTo(getId());
    message.setText("");
    message.setContentType(ContentType.STICKER);

    Map<String, String> metaData = new HashMap<String, String>();

    metaData.put("STKID", stickerId);
    metaData.put("STKPKGID", stickerPackageId);
    metaData.put("STKVER", stickerVersion);
    metaData.put("STKTXT", stickerText);
    message.setContentMetadata(metaData);

    _client.sendMessage(0, message);

    return true;
  }

  /**
   * Gets the id.
   * 
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the id.
   * 
   * @param id the new id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the client.
   * 
   * @return the client
   */
  public LineClient getClient() {
    return _client;
  }

  /**
   * Sets the client.
   * 
   * @param _client the new client
   */
  public void setClient(LineClient _client) {
    this._client = _client;
  }


  /**
   * Send image.
   * 
   * @param is the is
   * @return true, if successful
   * @throws Exception the exception
   */
  public boolean sendImage(InputStream is) throws Exception {
    /**
     * Send a image
     * 
     * :param path: local path of image to send
     **/
    try {
      LineMessage message = new LineMessage();
      message.setTo(getId());
      message.setText("");
      message.setContentType(ContentType.IMAGE);
      byte[] bytes = IOUtils.toByteArray(is);
      message.setContentPreview(bytes);

      Map<String, String> metaData = new HashMap<String, String>();

      String url = null;

      metaData.put("PREVIEW_URL", url);
      metaData.put("DOWNLOAD_URL", url);
      metaData.put("public", url);
      message.setContentMetadata(metaData);

      _client.sendMessage(0, message);

      return true;
    } catch (Exception e) {
      throw e;
    }
  }


  /**
   * Send a image with given image url
   * 
   * @param url the image url to send
   * @return true, if successful
   * @throws Exception the exception
   */
  public boolean sendImageWithURL(String url) throws Exception {
    if (StringUtils.isEmpty(url)) return false;
    try {
      HttpResponse<InputStream> response = Unirest.get(url).asBinary();
      if (response.getBody() != null) {
        return false;
      }
      LineMessage message = new LineMessage();
      message.setTo(getId());
      message.setText("");
      message.setContentType(ContentType.IMAGE);
      byte[] bytes = IOUtils.toByteArray(response.getBody());
      message.setContentPreview(bytes);

      Map<String, String> metaData = new HashMap<String, String>();
      metaData.put("PREVIEW_URL", url);
      metaData.put("DOWNLOAD_URL", url);
      metaData.put("public", "true");
      message.setContentMetadata(metaData);

      _client.sendMessage(1, message);

      return true;
    } catch (Exception e) {
      throw e;
    }
    // response = requests.get(url, stream=True)
    //
    // message = Message(to=self.id, text=None)
    // message.contentType = ContentType.IMAGE
    // message.contentPreview = response.raw.read()
    // #message.contentPreview = url.encode('utf-8')
    //
    // message.contentMetadata = {
    // 'PREVIEW_URL': url,
    // 'DOWNLOAD_URL': url,
    // '}public': "True",
    // }
    //
    // self._client.sendMessage(1, message)
    // return true;
  }

  public List<LineMessage> getRecentMessages(int count) throws Exception {
    /**
     * Get recent messages
     * 
     * :param count: count of messages to get
     **/
    TMessageBox messageBox = null;
    List<LineMessage> msgList = null;

    try {
      messageBox = _client.getMessageBox(getId());
      if (messageBox != null) {
        msgList = _client.getRecentMessages(messageBox, count);
      }
    } catch (Exception e) {
      throw e;
    } finally {
      return msgList;
    }


    // return messages
    // else{
    // self._messageBox = self._client.getMessageBox(self.id)
    // messages = self._client.getRecentMessages(self._messageBox, count)
    //
    // return messages

  }


}
