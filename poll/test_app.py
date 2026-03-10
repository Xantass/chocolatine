import json
import unittest
from unittest.mock import patch, MagicMock

from app import app


class PollAppTestCase(unittest.TestCase):
    def setUp(self):
        self.client = app.test_client()

    def test_get_root_sets_voter_cookie_and_returns_200(self):
        response = self.client.get("/")
        self.assertEqual(response.status_code, 200)
        self.assertIn("voter_id", response.headers.get("Set-Cookie", ""))

    @patch("app.get_redis")
    def test_post_vote_pushes_to_redis_and_returns_200(self, mock_get_redis):
        fake_redis = MagicMock()
        mock_get_redis.return_value = fake_redis

        response = self.client.post("/", data={"vote": "a"})

        self.assertEqual(response.status_code, 200)
        fake_redis.rpush.assert_called_once()
        args, _ = fake_redis.rpush.call_args
        self.assertEqual(args[0], "votes")


if __name__ == "__main__":
    unittest.main()

