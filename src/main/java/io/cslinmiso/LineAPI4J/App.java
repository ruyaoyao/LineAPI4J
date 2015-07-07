/**
 * 
 * @Package: io.cslinmiso.LineAPI4J
 * @FileName: App.java
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
package io.cslinmiso.LineAPI4J;

import io.cslinmiso.line.api.LineApi;
import io.cslinmiso.line.api.impl.LineApiImpl;
import io.cslinmiso.line.model.LineClient;
import io.cslinmiso.line.model.LineContact;
import line.thrift.LoginResult;

/**
 * LineAPI4J
 *
 */
public class App 
{
    public static void main( String[] args )
    {
      try {
        String id = "xxx@xxxx.com";
        String password = "xx";
        String certificate = "";
        
        // There are two ways to log in, by id and password.
        // if you have acquired certificate token, you could use it to login too. 
        // it will skip the pincode check.
        
        // init client
        LineClient client = new LineClient(id, password);
        LineContact someoneContact = client.getContactByName("someone"); // find contact
        System.out.println(someoneContact.sendSticker("13", "1", "100", ""));
        System.out.println(someoneContact.sendMessage(":) test"));

      } catch (java.net.SocketTimeoutException e) {
        // setAwaitforVerify false
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
}
