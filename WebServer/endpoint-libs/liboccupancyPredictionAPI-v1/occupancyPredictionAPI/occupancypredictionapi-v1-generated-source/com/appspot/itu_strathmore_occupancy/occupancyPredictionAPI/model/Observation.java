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
 * Model definition for Observation.
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
public final class Observation extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String camera;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private com.google.api.client.util.DateTime captureDate;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String day;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer maxContoursDetected;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long objectId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean occupancy;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer timeOfDay;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long unixCaptureTimestamp;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getCamera() {
    return camera;
  }

  /**
   * @param camera camera or {@code null} for none
   */
  public Observation setCamera(java.lang.String camera) {
    this.camera = camera;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public com.google.api.client.util.DateTime getCaptureDate() {
    return captureDate;
  }

  /**
   * @param captureDate captureDate or {@code null} for none
   */
  public Observation setCaptureDate(com.google.api.client.util.DateTime captureDate) {
    this.captureDate = captureDate;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getDay() {
    return day;
  }

  /**
   * @param day day or {@code null} for none
   */
  public Observation setDay(java.lang.String day) {
    this.day = day;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getMaxContoursDetected() {
    return maxContoursDetected;
  }

  /**
   * @param maxContoursDetected maxContoursDetected or {@code null} for none
   */
  public Observation setMaxContoursDetected(java.lang.Integer maxContoursDetected) {
    this.maxContoursDetected = maxContoursDetected;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getObjectId() {
    return objectId;
  }

  /**
   * @param objectId objectId or {@code null} for none
   */
  public Observation setObjectId(java.lang.Long objectId) {
    this.objectId = objectId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getOccupancy() {
    return occupancy;
  }

  /**
   * @param occupancy occupancy or {@code null} for none
   */
  public Observation setOccupancy(java.lang.Boolean occupancy) {
    this.occupancy = occupancy;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getTimeOfDay() {
    return timeOfDay;
  }

  /**
   * @param timeOfDay timeOfDay or {@code null} for none
   */
  public Observation setTimeOfDay(java.lang.Integer timeOfDay) {
    this.timeOfDay = timeOfDay;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getUnixCaptureTimestamp() {
    return unixCaptureTimestamp;
  }

  /**
   * @param unixCaptureTimestamp unixCaptureTimestamp or {@code null} for none
   */
  public Observation setUnixCaptureTimestamp(java.lang.Long unixCaptureTimestamp) {
    this.unixCaptureTimestamp = unixCaptureTimestamp;
    return this;
  }

  @Override
  public Observation set(String fieldName, Object value) {
    return (Observation) super.set(fieldName, value);
  }

  @Override
  public Observation clone() {
    return (Observation) super.clone();
  }

}
