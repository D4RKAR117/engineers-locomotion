# Get the new version from package.json using jq
new_version=$(cat package.json | jq -r '.version' )

# Exit if the new version is empty or doen't match the expected format semver format (should allow pre-release versions)
if [[ -z "$new_version" ]] || [[ ! "$new_version" =~ ^[0-9]+\.[0-9]+\.[0-9]+(-[a-zA-Z0-9.]+)?$ ]]; then
  echo "Error: The new version is empty or doesn't match the expected format semver format (x.y.z(-pre-release)?))"
  exit 1
fi

# Update the mod_version property in gradle.properties
sed -i "s/mod_version=.*/mod_version=$new_version/" gradle.properties

#  Add the bumped gradle.properties to the last commit ammending the files to the last commit
git add gradle.properties
git commit --amend --no-edit