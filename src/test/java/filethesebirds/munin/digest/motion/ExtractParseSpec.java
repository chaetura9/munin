package filethesebirds.munin.digest.motion;

import filethesebirds.munin.digest.Comment;
import filethesebirds.munin.digest.DigestTestUtils;
import filethesebirds.munin.digest.TaxResolve;
import java.util.Collections;
import java.util.Set;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class ExtractParseSpec {

  private static final String REVIEWER = "brohitbrose";
  private static final String SUGGESTER = "--";

  private static final TaxResolve TAXONOMY = new TaxResolve();

  private static Comment bareSuggestion(String body) {
    return DigestTestUtils.bareComment(SUGGESTER, body);
  }

  private static Comment bareReview(String body) {
    return DigestTestUtils.bareComment(REVIEWER, body);
  }

  @Test
  public void testFastPath() {
    final String suggestBody = "!addTaxa won'tbefound,     rethaw barowl13,shbdow\nlobdow";
    final String reviewBody = "!overrideTaxa won'tbefound,     rethaw barowl13,shbdow\nlobdow";
  }

  @Test
  public void testSlowPath() {
    final String body = "To me everything about this looks better for [young Broad-winged]"
        + "(https://media.ebird.org/catalog?taxonCode=brwhaw&sort=rating_rank_desc&mediaType=photo&beginMonth=8&endMonth=9&age=juvenile,immature)."
        + " u/TinyLongwing?\n\nNote that there's also [Northern Mockingbird]"
        + "(https://www.allaboutbirds.org/guide/Northern_Mockingbird/overview) in the last pic.";
    final Extract extract = ExtractParse.parseComment(TAXONOMY, bareSuggestion(body));
    assertEquals(extract.base().plusTaxa(), Set.of("brwhaw"));
    assertEquals(extract.hints(), Set.of("northern mockingbird"));
  }

  @Test
  public void testOverrideFastPathShortCircuit() {
    final String body = "!overrideTaxa blujay reshaw\n\nThe hawk is Red-shouldered, not Red-tailed. Even [immature RTHA]"
        + "(https://media.ebird.org/catalog?taxonCode=rethaw&sort=rating_rank_desc&mediaType=photo&age=immature,juvenile)"
        + " are super bulky and almost always have the belly band. Be mindful that [young Broad-winged]"
        + "(https://media.ebird.org/catalog?taxonCode=brwhaw&sort=rating_rank_desc&mediaType=photo&age=immature,juvenile)"
        + " may often look incredibly similar to this hawk.";
    final Extract extract = ExtractParse.parseComment(TAXONOMY, bareReview(body));
    assertEquals(extract.base().overrideTaxa(), Set.of("blujay", "reshaw"));
  }

  @Test
  public void testAddFastPathShortCircuit() {
    final String body = "The hawk is Red-shouldered, not Red-tailed. Even [immature RTHA]"
        + "(https://media.ebird.org/catalog?taxonCode=rethaw&sort=rating_rank_desc&mediaType=photo&age=immature,juvenile)"
        + " are super bulky and almost always have the belly band. Be mindful that [young Broad-winged]"
        + "(https://media.ebird.org/catalog?taxonCode=brwhaw&sort=rating_rank_desc&mediaType=photo&age=immature,juvenile)"
        + " may often look incredibly similar to this hawk.\n\n!addtaxa blujay reshaw\n\n!addTaxa shbdow";
    final Extract extract = ExtractParse.parseComment(TAXONOMY, bareReview(body));
    assertEquals(extract.base().plusTaxa(), Set.of("blujay", "reshaw"));
  }

  @Test
  public void testNr() {
    final String body = "++Setophaga sp++ or doing an addTaxa with the code from the link you should see soon would both work.\n\n!nr";
    final Extract extract = ExtractParse.parseComment(TAXONOMY, bareReview(body));
    assertEquals(extract.base().plusTaxa(), Collections.emptySet());
    assertEquals(extract.vagueHints(), Set.of("Setophaga sp"));
  }

  @Test
  public void testEscaped() {
    final String body = "\\++hybrid yellow dark junco++, nice find!\n\n"
        + "\\+Clay-colored        Sparrow+ behind that, and ++diurnal\traptor sp++ is the best we can do";
    final Extract extract = ExtractParse.parseComment(TAXONOMY, bareSuggestion(body));
    assertEquals(extract.vagueHints(), Set.of("hybrid yellow dark junco", "diurnal\traptor sp"));
    assertEquals(extract.hints(), Set.of("Clay-colored        Sparrow"));
  }

}
