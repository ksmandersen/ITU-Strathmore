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
 * Model definition for ObservationListContainer.
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
public final class ObservationListContainer extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<Observation> observationList;

  static {
    // hack to force ProGuard to consider Observation used, since otherwise it would be stripped out
    // see http://code.google.com/p/google-api-java-client/issues/detail?id=528
    com.google.api.client.util.Data.nullOf(Observation.class);
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<Observation> getObservationList() {
    return observationList;
  }

  /**
   * @param observationList observationList or {@code null} for none
   */
  public ObservationListContainer setObservationList(java.util.List<Observation> observationList) {
    this.observationList = observationList;
    return this;
  }

  @Override
  public ObservationListContainer set(String fieldName, Object value) {
    return (ObservationListContainer) super.set(fieldName, value);
  }

  @Override
  public ObservationListContainer clone() {
    return (ObservationListContainer) super.clone();
  }

}