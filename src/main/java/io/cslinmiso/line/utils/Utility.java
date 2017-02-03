/**
 * @Package: io.cslinmiso.line.model
 * @FileName: Utility.java
 * @author: treylin
 * @date: 2016/03/28, 下午 12:14:20
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
package io.cslinmiso.line.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Trey Lin
 */
public final class Utility {

  private static final String UTL_8 = "UTF-8";

  // 分割的tag
  public static final String splitTag = String.valueOf('\002');

  private Utility() throws InstantiationException {
    throw new InstantiationException("This utility class is not created for instantiation");
  }

  public static boolean isEmptyObject(JSONObject object) {
    return object.names() == null;
  }

  public static Map getMap(JSONObject object, String key) throws JSONException {
    return toMap(object.getJSONObject(key));
  }

  /*
   * 轉換JSONObject成Map
   */
  public static Map toMap(JSONObject object) throws JSONException {
    Map map = new LinkedHashMap();
    Iterator keys = object.keys();
    while (keys.hasNext()) {
      String key = (String) keys.next();
      if (object.get(key) instanceof JSONObject || object.get(key) instanceof JSONArray) {
        map.put(key, fromJson(object.get(key)));
      } else {
        map.put(key, object.get(key) == null ? null : String.valueOf(object.get(key)));
      }
    }
    return map;
  }

  public static List toList(JSONArray array) throws JSONException {
    List list = new ArrayList();
    for (int i = 0; i < array.length(); i++) {
      list.add(fromJson(array.get(i)));
    }
    return list;
  }

  private static Object fromJson(Object json) throws JSONException {
    if (json == JSONObject.NULL) {
      return null;
    } else if (json instanceof JSONObject) {
      return toMap((JSONObject) json);
    } else if (json instanceof JSONArray) {
      return toList((JSONArray) json);
    } else {
      return json;
    }
  }

  public static String bean2Json(Object obj) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    StringWriter sw = new StringWriter();
    JsonGenerator gen = new JsonFactory().createJsonGenerator(sw);
    mapper.writeValue(gen, obj);
    gen.close();
    return sw.toString();
  }

  public static <T> T json2Bean(String jsonStr, Class<T> objClass) throws JsonParseException,
      JsonMappingException, IOException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(jsonStr, objClass);
  }


  /**
   * 事前準備：import java.util.Date; import java.text.SimpleDateFormat; 主要是要存入 datebase 欄位格式為datetime的欄位
   */
  public static String getDateTime() {
    SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
    TimeZone tz = TimeZone.getTimeZone("Asia/Taipei");
    TimeZone.setDefault(tz);
    Date date = new Date();
    String strDate = sdFormat.format(date);
    return strDate;
  }

  public static Date getTaipeiDateTime() {
    TimeZone tz = TimeZone.getTimeZone("Asia/Taipei");
    TimeZone.setDefault(tz);
    return new Date();
  }

  public static String getQueryString(Map target) {
    StringBuilder sb = new StringBuilder();
    for (Object key : target.keySet()) {
      if ("_".equals(key)) continue;
      String[] t = (String[]) target.get(key);
      sb.append(String.format("%s=%s, ", String.valueOf(key), t[0]));
    }
    return sb.toString();
  }

  public static Map<String, String> getQueryMap(String query) {
    String[] params = query.split("&");
    System.out.println("query" + query);
    Map<String, String> map = new HashMap<String, String>();
    for (String param : params) {
      String name = param.split("=")[0];
      String value = param.split("=")[1];
      map.put(name, value);
    }
    return map;
  }

  public static String cryptWithMD5(String pass) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] passBytes = pass.getBytes(StandardCharsets.UTF_8);
      md.reset();
      byte[] digested = md.digest(passBytes);
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < digested.length; i++) {
        sb.append(Integer.toHexString(0xff & digested[i]));
      }
      return sb.toString();
    } catch (NoSuchAlgorithmException ex) {
      // 錯誤訊息
    }
    return null;
  }


  /**
   * 轉換已編碼後的字串，編碼格式為UTF-8
   * 
   * @param str
   * @return
   */
  public static String unescapeString(String str) {
    String result = null;
    try {
      result = URLDecoder.decode(str, UTL_8);
    } catch (UnsupportedEncodingException e) {
      System.out.println("轉碼錯誤：" + e.getMessage());
      result = null;
      e.printStackTrace();
    }
    return result;
  }

  /**
   * Regex 過濾器，找出需要字串
   * 
   * @param target
   * @param pattern
   * @return target || null
   */
  public static String regexFilter(String target, String pattern) {
    Pattern p = Pattern.compile(pattern);
    Matcher match = p.matcher(target);
    if (match.find()) {
      return match.group(1);
    } else {
      return null; // 找不到則回傳null
    }
  }

  /**
   * Java IO讀取檔案 (java io read file)
   * 
   * @param fileName
   * @return
   * @throws IOException
   */
  public static List<String> readFile(String fileName) throws IOException {
    List<String> rst = new ArrayList<String>();
    BufferedReader br = null;
    try {
      br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), UTL_8));
      String in = null;
      while ((in = br.readLine()) != null) {
        rst.add(in);
      }
    } catch (IOException ioe) {
      throw ioe;
    } finally {
      br.close();
    }
    return rst;
  }

  public static void writeFile(List<String> data, File f) throws IOException {
    writeFile(data, f.getAbsolutePath());
  }

  /**
   * Java IO寫入檔案 (java io write file)
   * 
   * @param data
   * @param fileName
   * @throws IOException
   */
  public static void writeFile(List<String> data, String fileName) throws IOException {
    BufferedWriter bw =
        new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), UTL_8));
    try {
      for (String d : data) {
        bw.write(d);
        bw.newLine();
      }
      bw.flush();
    } catch (IOException ioe) {
      throw ioe;
    } finally {
      bw.close();
    }
  }

  /**
   * Java IO寫入檔案 (java io write file)
   * 
   * @param inputData
   * @param filePath
   * @throws IOException
   */
  public static void writeFile(InputStream inputData, String filePath) throws IOException {
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(filePath);
      int size;
      byte[] buffer = new byte[1024];
      while ((size = inputData.read(buffer)) != -1) {
        fos.write(buffer, 0, size);
      }
      fos.flush();
    } catch (IOException ioe) {
      throw ioe;
    } finally {
      if (fos != null) {
        fos.close();
      }
    }
  }
}
