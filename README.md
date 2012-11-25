##CloSearch

A very basic text indexer / search server

##How do I use it?

Be warned: there is not much you can do with this besides telling me all I could be doing better (please do). I had the idea of writing the basics of a search engine in Clojure just as a learning exercise. That said:

+ Install leiningen if you don't have it.

+ Create a directory named "text" whenever you'll be running the server. Throw a bunch of text files in it.

+ "lein run" will index those files and start a jetty server on localhost:8080.

+ Point your browser to http://localhost:8080?the+words+to+search to see list of the files that match those words.

Perhaps I'll turn this into something more useful, who knows :)


