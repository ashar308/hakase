package study.hakase.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Service
class ArXivMetadataService {

    private val client = HttpClient.newHttpClient()

    private fun searchArXivByID(ID: String): Map<String, Any> {

        val request = HttpRequest.newBuilder().uri(URI("http://export.arxiv.org/api/query?id_list=$ID")).GET().build()
        val xml = client.send(request, HttpResponse.BodyHandlers.ofString()).body()

        return XmlMapper().readValue(xml, mutableMapOf<String, Any>().javaClass)
    }

    fun getEntryfromArXiv(ID: String) = searchArXivByID(ID)["entry"] as Map<String, Any>

    fun getTitle(ID: String) = getEntryfromArXiv(ID)["title"].toString()

    fun getAbsLink(ID: String) = getEntryfromArXiv(ID)["id"].toString()

    fun getPDFLink(ID: String): String {
        val entry = this.getEntryfromArXiv(ID)
        val links = entry["link"] as List<Map<String, Any>>

        for (link in links) {
            if (link["title"] === "pdf") return "${link["href"]}.pdf"
        }

        return "https://arxiv.org/pdf/$ID.pdf"
    }

    fun getJSON(ID: String) = ObjectMapper().writeValueAsString(getEntryfromArXiv(ID))
}