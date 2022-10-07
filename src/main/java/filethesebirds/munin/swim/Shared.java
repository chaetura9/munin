// Copyright 2015-2022 Swim.inc
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package filethesebirds.munin.swim;

import filethesebirds.munin.Main;
import filethesebirds.munin.connect.ebird.EBirdClient;
import filethesebirds.munin.connect.reddit.RedditApiException;
import filethesebirds.munin.connect.reddit.RedditClient;
import java.net.http.HttpClient;

/**
 * Utility class containing singleton clients to external APIs.
 */
public class Shared {

  private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

  private Shared() {
  }

  private static EBirdClient eBirdClient = null;

  private static RedditClient redditClient = null;

  public static HttpClient httpClient() {
    return HTTP_CLIENT;
  }

  public static EBirdClient eBirdClient() {
    return Shared.eBirdClient;
  }

  public static RedditClient redditClient() {
    return Shared.redditClient;
  }

  public static void loadEBirdClient() {
    if (Shared.eBirdClient != null) {
      throw new IllegalStateException("Multiple eBird client loading forbidden");
    }
    Shared.eBirdClient = EBirdClient.fromResource(httpClient(), Main.class,
        "/ebird-config.properties");
  }

  public static void loadRedditClient() throws RedditApiException {
    if (Shared.redditClient != null) {
      throw new IllegalStateException("Multiple Reddit client loading forbidden");
    }
    Shared.redditClient = RedditClient.fromResource(httpClient(), Main.class,
        "/reddit-config.properties");
  }

}
