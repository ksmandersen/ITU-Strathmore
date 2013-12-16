/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2013-11-22 19:59:01 UTC)
 * on 2013-12-06 at 11:55:41 UTC 
 * Modify at your own risk.
 */

package com.appspot.itu_strathmore_occupancy.occupancyPredictionAPI.model;

/**
 * Model definition for UploadUrl.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the occupancyPredictionAPI. For a detailed explanation
 * see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class UploadUrl extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String camera;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String date;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String url;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getCamera() {
    return camera;
  }

  /**
   * @param camera camera or {@code null} for none
   */
  public UploadUrl setCamera(java.lang.String camera) {
    this.camera = camera;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getDate() {
    return date;
  }

  /**
   * @param date date or {@code null} for none
   */
  public UploadUrl setDate(java.lang.String date) {
    this.date = date;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getUrl() {
    return url;
  }

  /**
   * @param url url or {@code null} for none
   */
  public UploadUrl setUrl(java.lang.String url) {
    this.url = url;
    return this;
  }

  @Override
  public UploadUrl set(String fieldName, Object value) {
    return (UploadUrl) super.set(fieldName, value);
  }

  @Override
  public UploadUrl clone() {
    return (UploadUrl) super.clone();
  }

}