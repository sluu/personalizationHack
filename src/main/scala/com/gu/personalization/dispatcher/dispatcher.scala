package com.gu.personalization
package dispatcher

import org.scalatra.{ScalatraKernel, ScalatraFilter}
import com.gu.openplatform.contentapi.Api
import scala.collection.JavaConversions._
import com.gu.openplatform.contentapi.model.Content
import com.gu.openplatform.contentapi.connection.JavaNetHttp
import org.scalatra.scalate.ScalateSupport
import collection.immutable.HashMap

class Dispatcher extends ScalatraFilter with ScalateSupport with Logging {

  get("/"){
    "HELLO WORLD"
  }


  post("/saveProfile") {
    val userId = params.getOrElse("userId", throw new Exception("missing userid"))
    val terms = multiParams.get("terms").getOrElse(Seq()).mkString(",").toLowerCase

    model.Personalization.save(userId, terms)
    redirect(request.referrer.get)
  }

  get("/personalization/:userId"){
    val userId = params.getOrElse("userId", throw new Exception("missing userid"))
    val terms = model.Personalization.get(userId).split(",")
    try{
      val tags = terms.map(ApiClient.getTagId(_)).mkString("|")
      val content = ApiClient.searchApi(tags)
      val renderParams = Map("content" -> content, "userid" -> userId)
      render("templates/personalizedPage", renderParams)
      // renderTemplate("templates/personalizedPage.ssp", "content" -> content, "userid" -> userId)

    }catch{
      case e => {
        log.error(e.getMessage)
        log.error(e.getStackTraceString)
        terms
      }
    }
  }

  get("/personalization2/:userId"){
      val userId = params.getOrElse("userId", throw new Exception("missing userid"))
      val terms = model.Personalization.get(userId).split(",")
      var result = Map[String, List[Content]]()
      try{
        terms.foreach(term => {
          val tag = ApiClient.getTagId(term)
          val content = ApiClient.searchApiWithLimit(tag, Some(3))
          result += term -> content
        })

        val renderParams = Map("result" -> result, "userid" -> userId)
        
        render2(renderParams)
        

      }catch{
        case e => {
          log.error(e.getMessage)
          log.error(e.getStackTraceString)
          terms
        }
      }
    }

  get("/personalization/:userId/savedTags"){
    val userId = params.getOrElse("userId", throw new Exception("missing userid"))
    val terms = model.Personalization.get(userId).replaceAll(",","\n")
    terms
  }

  get("/personalization/:userId/savedTagIds"){
    val userId = params.getOrElse("userId", throw new Exception("missing userid"))
    val terms = model.Personalization.get(userId).split(",")
    try{
      val tags = terms.map(ApiClient.getTagId(_)).mkString("\n")
      tags

    }catch{
      case e => {
        log.error(e.getMessage)
        log.error(e.getStackTraceString)
        terms
      }
    }
  }


  def render(file: String, renderParams: Map[String, Any] = Map(), contentTypeHeader: String = "text/html;charset=UTF-8", cacheMaxAge: Int = 0) = {
    response.setHeader("Cache-Control", "public, max-age=%d" format cacheMaxAge)
    contentType = contentTypeHeader
    //templateEngine.layout("/WEB-INF/scalate/%s.ssp" format file, renderParams)
    val htmlString = "<html><head><title></title></head><body><div><ul>" +
      renderParams.get("content").get.asInstanceOf[List[com.gu.openplatform.contentapi.model.Content]].map(articleHtml(_)).mkString("") +
      "</div></body></html>"
    htmlString
  }


  def articleHtml(article: com.gu.openplatform.contentapi.model.Content) : String = {
    log.info(article.webTitle)
    val pstyle = "<p style=\"border-bottom: 1px dotted #000000; width: 300px;\">"
    val titleLink = "<a style=\"text-decoration: none;color: #005689;font-size: 12px;font-family: arial,sans-serif;\" href=\""+article.webUrl+"\">"+article.webTitle+"</a>\n"
    val thumbnail = article.fields.flatMap(_.get("thumbnail"))
    if(!thumbnail.isEmpty){
      pstyle+"<img style=\"float:left\" src=\""+thumbnail.get+"\"/>\n"+titleLink+"</p></li>"
    }
    else pstyle+titleLink+"</p></li>"


  }
  
  def render2(renderParams: Map[String, Any] = Map(), contentTypeHeader: String = "text/html;charset=UTF-8", cacheMaxAge: Int = 0) = {
    response.setHeader("Cache-Control", "public, max-age=%d" format cacheMaxAge)
    contentType = contentTypeHeader

    val result = renderParams.get("result").get.asInstanceOf[Map[String, List[Content]]]
    val content = new StringBuilder()
    for ((key, value) <- result) {
      content.append("<h1>").append(key).append("</h1>")
      content.append(value.map(articleHtml(_)).mkString).append(" <br> ")
    }

    val htmlString = "<html><head><title></title></head><body>" + content.toString()+
      "</body></html>"
    htmlString
  }

}


object ApiClient extends Api with JavaNetHttp with Logging  {

  apiKey = Some("techdev-internal")

  def searchApi(tagstring : String) : List[Content]  = {

    log.info(tagstring)
    val apiRequest = Api.search
                      .tag(tagstring)
                      .showFields("all")

    apiRequest.results

  }

  def searchApiWithLimit(tagstring : String, pageSize: Option[Int]) : List[Content]  = {

      log.info(tagstring)
      val apiRequest = Api.search
                        .tag(tagstring)
                        .showFields("all")
                        .pageSize(pageSize.getOrElse(10))

      apiRequest.results

    }

  def getTagId(term : String) : String = {

    log.info(term)
    val apiRequest = Api.tags
                    .q(term)
    val firstResult : Option[com.gu.openplatform.contentapi.model.Tag] = apiRequest.results.headOption
    val tagId = firstResult.map(_.id).getOrElse("")
    log.info(tagId)
    tagId
  }

}
