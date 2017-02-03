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
 * Copyright (c) 2016 Trey Lin
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import io.cslinmiso.line.api.LineApi;
import io.cslinmiso.line.api.impl.LineApiImpl;
import line.thrift.ContentType;
import line.thrift.Message;
import line.thrift.TMessageBox;
import line.thrift.TalkException;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The abstract Class LineBase.
 */
public abstract class LineBase {

  /** The id. */
  public String id;

  /** The _client. */
  public LineClient client;

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
    client.sendMessage(0, message);

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

    client.sendMessage(0, message);

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
    return client;
  }

  /**
   * Sets the client.
   * 
   * @param client the new client
   */
  public void setClient(LineClient client) {
    this.client = client;
  }

  /**
   * Send image by path.
   * 
   * @param path is local path of image to send
   * @return true, if successful
   * @throws Exception the exception
   */
  public boolean sendImage(String path) throws Exception {
    return sendImage(new File(path));
  }

  /**
   * Send image.
   * 
   * @param file is File
   * @return true, if successful
   * @throws Exception the exception
   */
  public boolean sendImage(File file) throws Exception {
    return sendImage(new BufferedInputStream(new FileInputStream(file)));
  }

  /**
   * Send image.
   * 
   * @param is the is
   * @return true, if successful
   * @throws Exception the exception
   */
  public boolean sendImage(InputStream is) throws Exception {
    try {
      LineMessage message = new LineMessage();
      message.setTo(getId());
      message.setText("");
      message.setContentType(ContentType.IMAGE);

      Message sendMessage = client.sendMessage(0, message);
      String messageId = sendMessage.getId();

      // preparing params which is detail of image to upload server
      ObjectMapper objectMapper = new ObjectMapper();
      ObjectNode objectNode = objectMapper.createObjectNode();
      objectNode.put("name", "media");
      objectNode.put("oid", messageId);
      objectNode.put("size", is.available());
      objectNode.put("type", "image");
      objectNode.put("ver", "1.0");

      Map<String, Object> data = new HashMap<String, Object>();
      // data.put("file", file);
      data.put("params", objectMapper.writeValueAsString(objectNode));

      String url = LineApi.LINE_UPLOADING_URL;
      LineApiImpl api = (LineApiImpl) client.getApi();
      boolean isUploaded = api.postContent(url, data, is);

      if (isUploaded == false) {
        throw new Exception("Fail to upload image.");
      }
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
      InputStream is = response.getBody();
      if (is == null) {
        return false;
      }

      sendImage(is);

      return true;
    } catch (Exception e) {
      throw e;
    }
  }

  public boolean sendFile(String path) throws Exception {
    return sendFile("", path);
  }

  public boolean sendFile(File file) throws Exception {
    return sendFile("", file);
  }

  /**
   * Send file.
   * 
   * @param String name
   * @param String path
   * @return true, if successful
   * @throws Exception the exception
   */
  public boolean sendFile(String name, String path) throws Exception {
    File tmpFile = new File(path);
    return sendFile(name, tmpFile);
  }

  /**
   * Send file.
   * 
   * @param String name
   * @param String path
   * @return true, if successful
   * @throws Exception the exception
   */
  public boolean sendFile(String name, File file) throws Exception {
    if (!file.exists()) {
      throw new Exception("File is not exist.");
    }
    String fileName;
    try {
      if (StringUtils.isNotEmpty(name)) {
        fileName = name;
      } else {
        fileName = file.getName();
      }
      sendFile(fileName, new BufferedInputStream(new FileInputStream(file)));
      return true;
    } catch (Exception e) {
      throw e;
    }
  }

  public boolean sendFile(String name, InputStream is) throws Exception {
    String fileName = "SendByLineAPI4J";
    String fileSize = String.valueOf(is.available());
    try {
      if (StringUtils.isNotEmpty(name)) {
        fileName = name;
      }

      LineMessage message = new LineMessage();
      message.setTo(getId());
      message.setContentType(ContentType.FILE);

      Map<String, String> contentMetadata = new HashMap<String, String>();
      contentMetadata.put("FILE_NAME", fileName);
      contentMetadata.put("FILE_SIZE", fileSize);
      message.setContentMetadata(contentMetadata);

      Message sendMessage = client.sendMessage(0, message);
      String messageId = sendMessage.getId();

      // preparing params which is detail of image to upload server
      ObjectMapper objectMapper = new ObjectMapper();
      ObjectNode objectNode = objectMapper.createObjectNode();
      objectNode.put("name", fileName);
      objectNode.put("oid", messageId);
      objectNode.put("size", fileSize);
      objectNode.put("type", "file");
      objectNode.put("ver", "1.0");

      Map<String, Object> data = new HashMap<String, Object>();
      data.put("params", objectMapper.writeValueAsString(objectNode));

      String url = LineApi.LINE_UPLOADING_URL;
      LineApiImpl api = (LineApiImpl) client.getApi();
      boolean isUploaded = api.postContent(url, data, is);

      if (isUploaded == false) {
        throw new Exception("Fail to upload file.");
      }
      return true;
    } catch (Exception e) {
      throw e;
    }
  }

  /**
   * Send a file with given file url
   * 
   * @param url the file url to send
   * @return true, if successful
   * @throws Exception the exception
   */
  public boolean sendFileWithURL(String url) throws Exception {
    if (StringUtils.isEmpty(url)) return false;
    try {
      HttpResponse<InputStream> response = Unirest.get(url).asBinary();
      InputStream is = response.getBody();
      if (is == null) {
        return false;
      }
      return sendFile(url.substring(url.lastIndexOf("/") + 1), is);
    } catch (Exception e) {
      throw e;
    }
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
      messageBox = client.getMessageBox(getId());
      if (messageBox != null) {
        msgList = client.getRecentMessages(messageBox, count);
      }
    } catch (Exception e) {
      throw e;
    } finally {
      return msgList;
    }
  }


}
