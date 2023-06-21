package study.hakase.services

import org.springframework.stereotype.Service
import java.net.MalformedURLException
import java.net.URL

@Service
class SourceParsingService {

    fun parseSource(source: String): Pair<String, String> {
        var site = "arxiv"
        try {
            val url = URL(source)

            val host = url.host.split('.')
            site = host[host.size - 2]

            val path = url.path.split('/')
            return Pair(site.replace('x', 'X'), path[path.size - 1])
        } catch (e: MalformedURLException) {
            val split = source.split(':')
            if (split.size == 2) return Pair(split[0].replace('x', 'X'), split[1])
        }
        return Pair(site.replace('x', 'X'), source)
    }
}