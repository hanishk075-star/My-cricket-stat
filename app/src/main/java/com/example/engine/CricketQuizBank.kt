package com.example.engine

import com.example.data.model.QuizQuestion

object CricketQuizBank {
    val questions: List<QuizQuestion> = listOf(
        QuizQuestion(
            id = "q1",
            question = "How many legal deliveries are there in a standard completed cricket over?",
            options = listOf("4 deliveries", "6 deliveries", "8 deliveries", "10 deliveries"),
            correctIndex = 1,
            explanation = "Under MCC Law 17, an over consists of 6 legal deliveries. Wides and no-balls do not count toward this total.",
            difficulty = "Easy",
            category = "Basic Rules"
        ),
        QuizQuestion(
            id = "q2",
            question = "If a batsman scores 45 runs off 30 balls, what is their batting strike rate?",
            options = listOf("133.33", "145.00", "150.00", "166.67"),
            correctIndex = 2,
            explanation = "Strike Rate = (Runs Scored / Balls Faced) * 100 = (45 / 30) * 100 = 150.00.",
            difficulty = "Easy",
            category = "Scoring & Stats"
        ),
        QuizQuestion(
            id = "q3",
            question = "A bowler concedes 26 runs in 4.2 overs. What is their economy rate?",
            options = listOf("5.85", "6.00", "6.19", "6.50"),
            correctIndex = 1,
            explanation = "4.2 overs equals 4 completed overs + 2 balls = 26 legal balls. Economy = 26 runs / (26 / 6) overs = 6.00.",
            difficulty = "Medium",
            category = "Scoring & Stats"
        ),
        QuizQuestion(
            id = "q4",
            question = "Which dismissal mode does NOT credit a wicket to the bowler's bowling statistics?",
            options = listOf("Stumped", "Hit Wicket", "Run Out", "LBW"),
            correctIndex = 2,
            explanation = "Run outs are credited to the fielders who execute them and never to the bowler's wickets tally.",
            difficulty = "Easy",
            category = "Dismissals"
        ),
        QuizQuestion(
            id = "q5",
            question = "In an LBW appeal, what is the critical rule regarding where the ball pitches?",
            options = listOf(
                "The ball can pitch anywhere on the turf",
                "The ball must NOT pitch outside the line of leg stump",
                "The ball must always pitch on middle stump",
                "The ball must pitch outside off stump only"
            ),
            correctIndex = 1,
            explanation = "Under MCC Law 36, if the ball pitches outside the line of leg stump, the batter cannot be given out LBW under any circumstances.",
            difficulty = "Medium",
            category = "Dismissals"
        ),
        QuizQuestion(
            id = "q6",
            question = "What is the standard length of a cricket pitch between the wickets?",
            options = listOf("20 yards (18.29 m)", "22 yards (20.12 m)", "24 yards (21.95 m)", "25 yards (22.86 m)"),
            correctIndex = 1,
            explanation = "According to MCC Law 6, the length of the pitch between bowling creases is exactly 22 yards (20.12 metres or 1 chain).",
            difficulty = "Easy",
            category = "Basic Rules"
        ),
        QuizQuestion(
            id = "q7",
            question = "If a batsman hits a shot, completes 2 physical runs, and a fielder's throw crosses the boundary rope, how many total runs are scored?",
            options = listOf("4 runs", "5 runs", "6 runs", "7 runs"),
            correctIndex = 2,
            explanation = "Under MCC Law 19, the boundary 4 runs are added to the runs already completed by the batsmen at the moment of the throw (2 + 4 = 6 runs).",
            difficulty = "Hard",
            category = "Basic Rules"
        ),
        QuizQuestion(
            id = "q8",
            question = "What is a 'Googly' bowled by a right-arm leg-spinner?",
            options = listOf(
                "A ball that bounces extra high without spinning",
                "A ball that turns into a right-handed batsman (from off to leg)",
                "A fast ball delivered with a straight arm",
                "A ball that spins away from a right-handed batsman"
            ),
            correctIndex = 1,
            explanation = "A googly (or wrong'un) looks like a leg-break out of the hand but spins the opposite way, breaking from off to leg into the right-hander.",
            difficulty = "Medium",
            category = "Terminology"
        ),
        QuizQuestion(
            id = "q9",
            question = "If a batsman remains 'Not Out' in all 5 innings of a season and scores 150 total runs, what is their calculated batting average?",
            options = listOf(
                "30.00",
                "150.00",
                "Undefined / Not applicable (displayed as --)",
                "0.00"
            ),
            correctIndex = 2,
            explanation = "Batting Average is Total Runs / Dismissals. With 0 dismissals, division by zero is avoided and standard cricket notation displays '--'.",
            difficulty = "Hard",
            category = "Scoring & Stats"
        ),
        QuizQuestion(
            id = "q10",
            question = "What is the minimum lead required in the first innings of a 5-day Test match to enforce a follow-on?",
            options = listOf("100 runs", "150 runs", "200 runs", "250 runs"),
            correctIndex = 2,
            explanation = "In a 5-day Test match, the side batting first can enforce the follow-on if they lead by at least 200 runs (MCC Law 14).",
            difficulty = "Medium",
            category = "Formats"
        )
    )
}
