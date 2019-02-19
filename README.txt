"Auto Test Tagless with Discipline and ScalaCheck"

Description: In this talk, we'll explore using the testing library Discipline
(with ScalaCheck) to automatically test "laws" (i.e. rules) against an example
final tagless algebra. Using a more generic rule testing approach, we will write
less testing code and achieve higher code coverage. This talk builds on the
October talk "Finally! Tagless and Fancy Free Monads" (https://github.com/lancegatlin/tagless-final-talk-03Oct18).
It is based on the ideas from Marcin Rzeźnicki in his blog post "Tagless with
Discipline — Testing Scala Code The Right Way" (https://medium.com/iterators/tagless-with-discipline-testing-scala-code-the-right-way-e74993a0d9b1).

At the end of this talk, you'll know:
* How to write Discipline laws to test a final tagless algebra
* How this approach reduces the amount of explicit testing code needed
* How to test laws against different monadic contexts (e.g. Id, Future, DBIO, etc)

Also recommended is a quick review of these before the talk:
* Monads
* Tagless final basics
* ScalaCheck

About Lance

Lance is a professional Scala adventurer, open source loner, longevity nut and
semi-regular awkward dancer. Lance has been exploring Scala for the last 7 years
 and recently renewed his Scala vows on his Scala-versary: "I'll never quit
 you... not like those others". Lance enjoys polishing his shiny sword of
 functional programming +2 but finds his patrons prefer just "getting things
 done" with their rusty daggers. Lance also used to do a lot of improv, has
 deep thoughts about one-handed typing and will win, eventually, by living
 longer than everyone else.