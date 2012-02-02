package com.gu.personalization
package model

import com.google.appengine.api.datastore._

object Personalization {

  val datastore = DatastoreServiceFactory.getDatastoreService();

  def save(userProfileId: String,  terms: String) {

    val profile = new Entity("Profile")
    profile.setProperty("userid", userProfileId)
    profile.setProperty("terms", terms)
    datastore.put(profile)
  }

  def get(userProfileId: String) : String = {
    val q = new Query("Profile");
    q.addFilter("userid", Query.FilterOperator.EQUAL, userProfileId);
    val pq = datastore.prepare(q);
    pq.asSingleEntity.getProperty("terms").asInstanceOf[String]
  }

}
