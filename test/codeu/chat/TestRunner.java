// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package codeu.chat;

import codeu.chat.server.Database;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.sql.SQLException;

public final class TestRunner {
  public static void main(String[] args) {

    // Clear the database
    Database db = new Database("testDatabase.db");
    try {
      db.executeUpdate("DELETE FROM users");
      db.executeUpdate("DELETE FROM conversations");
      db.executeUpdate("DELETE FROM messages");
      db.close();
    } catch (SQLException ex) {

    }

    // Run the tests
    final Result result =
      JUnitCore.runClasses(
        codeu.chat.common.SecretTest.class,
        codeu.chat.common.UuidTest.class,
        codeu.chat.common.UuidsTest.class,
        codeu.chat.relay.ServerTest.class,
        codeu.chat.server.BasicControllerTest.class,
        codeu.chat.server.RawControllerTest.class,
        codeu.chat.server.DatabaseTest.class,
        codeu.chat.util.store.StoreTest.class
      );
    if (result.wasSuccessful()) {
      System.out.println("\nAll Tests Passed");
    } else {
      System.out.println("\nFailures:");
      for (final Failure failure : result.getFailures()) {
        System.out.println(failure.toString());
      }
    }

  }
}
